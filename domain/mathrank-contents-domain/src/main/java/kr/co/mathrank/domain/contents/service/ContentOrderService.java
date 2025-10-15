package kr.co.mathrank.domain.contents.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.contents.dto.ContentOrderCommand;
import kr.co.mathrank.domain.contents.dto.ContentOrderQueryResult;
import kr.co.mathrank.domain.contents.entity.Content;
import kr.co.mathrank.domain.contents.entity.ContentOrder;
import kr.co.mathrank.domain.contents.entity.OrderStatus;
import kr.co.mathrank.domain.contents.exception.CannotFoundContentException;
import kr.co.mathrank.domain.contents.exception.ContentPurchaseException;
import kr.co.mathrank.domain.contents.repository.ContentOrderRepository;
import kr.co.mathrank.domain.contents.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ContentOrderService {
	private final ContentRepository contentRepository;
	private final ContentOrderRepository contentOrderRepository;

	private final TransactionalOutboxPublisher outboxPublisher;

	/**
	 * 콘텐츠 주문을 위한 API
	 *
	 * 이미 결제 중이거나, 완료된 콘텐츠에 대한 결제시도는 불가능합니다.
	 *
	 * @param command
	 * @return
	 */
	@Transactional
	public Long purchase(@NotNull @Valid final ContentOrderCommand command) {
		final Content content = contentRepository.findByIdForShare(command.contentId())
			.orElseThrow(() -> {
				log.info("[ContentOrderService.purchase] cannot found content - contentId: {}", command.contentId());
				return new CannotFoundContentException();
			});

		// 이미 결제 진행중이거나, 성공했으면 추가 결제 X
		// 동시 요청 시, 중복 결제가 시도될 위험이 있긴 하나, 극히 희박함
		//		- 클라이언트가 idempotence key를 의도적으로 바꾼 경우에만 이와 같은 위험 발생
		if(isAlreadyInProcessOrFinished(command)) {
			log.info("[ContentOrderService.purchase] purchase is already in process or finished - contentId: {}, memberId: {}", command.contentId(), command.memberId());
			throw new ContentPurchaseException("자료가 이미 결제중입니다.");
		}

		// idempotence key에 유니크 제약조건 -> 중복 주문 방지
		final ContentOrder order = ContentOrder.create(content, command.memberId(), command.idempotencyKey());
		try {
			contentOrderRepository.saveAndFlush(order);
		} catch (DataIntegrityViolationException e) {
			log.info("[ContentOrderService.purchase] duplicated idempotency key - contentId: {}, memberId: {}, idempotencyKey: {}", command.contentId(), command.memberId(), command.idempotencyKey());
			throw new ContentPurchaseException("멱등키가 중복되는 결제입니다. 다른 키로 다시 시도해주세요");
		}

		// 주문 생성 이벤트 발행
		outboxPublisher.publish("mathrank-content-order-registered", ContentOrderRegisteredEvent.from(order));

		return order.getId();
	}

	// 결제 완료 이벤트 수신
	@Transactional
	public void completeOrderToSucceed(@NotNull final Long orderId, @NotNull final BigDecimal purchasedPointAmount) {
		final ContentOrder contentOrder = getPendingOrder(orderId);
		contentOrder.succeed(LocalDateTime.now(), purchasedPointAmount);
	}

	// 결제 실패 이벤트 수신
	@Transactional
	public void completeOrderToFailed(@NotNull final Long orderId) {
		final ContentOrder contentOrder = getPendingOrder(orderId);
		contentOrder.failed(LocalDateTime.now());
	}

	private ContentOrder getPendingOrder(Long orderId) {
		return contentOrderRepository.findByOrderIdForUpdate(orderId, OrderStatus.PENDING)
			.orElseThrow(() -> {
				log.info("[ContentOrderService.getPendingOrder] cannot found pending order - orderId: {}", orderId);
				return new ContentPurchaseException("지연중인 주문을 찾을 수 없습니다.");
			});
	}

	// 결제 상태 조회 api
	public ContentOrderQueryResult queryOrder(
		@NotNull final Long orderId,
		@NotNull final Long requestMemberId
	) {
		return contentOrderRepository.findById(orderId)
			.filter(order -> order.getUserId().equals(requestMemberId))
			.map(ContentOrderQueryResult::from)
			.orElseThrow(() -> {
				log.info("[ContentOrderService.queryOrder] cannot access to order status info - orderId: {}, memberId: {}", orderId, requestMemberId);
				return new ContentPurchaseException("존재하지 않는 주문이거나, 본인의 주문이 아닙니다.");
			});
	}

	private boolean isAlreadyInProcessOrFinished(final ContentOrderCommand command) {
		return contentOrderRepository.findByContentIdAndUserIdAndOrderStatusForShare(
				command.contentId(),
				command.memberId(),
				List.of(OrderStatus.SUCCEEDED, OrderStatus.PENDING))
			.isPresent();
	}

	record ContentOrderRegisteredEvent(
		Long orderId,
		Long memberId,
		Long contentId,
		Long contentPointCost,
		LocalDateTime registerAt
	) implements EventPayload {
		static ContentOrderRegisteredEvent from(final ContentOrder order) {
			return new ContentOrderRegisteredEvent(
				order.getId(),
				order.getUserId(),
				order.getContent().getId(),
				order.getContent().getPrice().longValue(),
				order.getCreatedAt()
			);
		}
	}
}
