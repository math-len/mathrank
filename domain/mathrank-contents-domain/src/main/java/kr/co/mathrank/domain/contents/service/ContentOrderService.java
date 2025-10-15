package kr.co.mathrank.domain.contents.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.contents.dto.ContentOrderCommand;
import kr.co.mathrank.domain.contents.entity.Content;
import kr.co.mathrank.domain.contents.entity.ContentOrder;
import kr.co.mathrank.domain.contents.entity.OrderStatus;
import kr.co.mathrank.domain.contents.repository.ContentOrderRepository;
import kr.co.mathrank.domain.contents.repository.ContentRepository;
import lombok.RequiredArgsConstructor;

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
			.orElseThrow();

		// 이미 결제 진행중이거나, 성공했으면 추가 결제 X
		// 동시 요청 시, 중복 결제가 시도될 위험이 있긴 하나, 극히 희박함
		//		- 클라이언트가 idempotence key를 의도적으로 바꾼 경우에만 이와 같은 위험 발생
		if(isAlreadyInProcessOrFinished(command)) {
			throw new IllegalArgumentException();
		}

		final ContentOrder order = ContentOrder.create(content, command.memberId(), command.idempotenceKey());
		contentOrderRepository.save(order);

		// 주문 생성 이벤트 발행
		outboxPublisher.publish("mathrank-content-order-registered", ContentOrderRegisteredEvent.from(order));

		return order.getId();
	}

	private boolean isAlreadyInProcessOrFinished(final ContentOrderCommand command) {
		return contentOrderRepository.findByContentIdAndUserIdAndOrderStatusForShare(
				command.contentId(),
				command.memberId(),
				List.of(OrderStatus.SUCCEEDED, OrderStatus.PENDING))
			.isPresent();
	}

	record ContentOrderRegisteredEvent(
		Long memberId,
		Long contentId,
		Long contentPointCost,
		LocalDateTime registerAt
	) implements EventPayload {
		static ContentOrderRegisteredEvent from(final ContentOrder order) {
			return new ContentOrderRegisteredEvent(
				order.getContent().getId(),
				order.getUserId(),
				order.getContent().getPrice().longValue(),
				order.getCreatedAt()
			);
		}
	}
}
