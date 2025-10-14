package kr.co.mathrank.domain.point.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.point.dto.PointPurchaseCommand;
import kr.co.mathrank.domain.point.entity.PointChargedLog;
import kr.co.mathrank.domain.point.entity.PointProduct;
import kr.co.mathrank.domain.point.entity.UserPoint;
import kr.co.mathrank.domain.point.exception.PointPurchaseException;
import kr.co.mathrank.domain.point.repository.PointChargedLogRepository;
import kr.co.mathrank.domain.point.repository.PointProductRepository;
import kr.co.mathrank.domain.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointPurchaseService {
	private final PointProductRepository pointProductRepository;
	private final PointChargedLogRepository pointChargedLogRepository;
	private final UserPointRepository userPointRepository;
	private final PortOnePaymentClient portOnePaymentClient;

	/**
	 * 결제 내용을 확인하고 검증하여 상품을 배정할지 결정하는 api
	 * @param command
	 */
	@Transactional(noRollbackFor = PointPurchaseException.class)
	public void confirm(@NotNull @Valid final PointPurchaseCommand command) {
		// 1. 결재 내역 조회
		final PortOnePaymentClient.PaymentInfo paymentInfo = portOnePaymentClient.getPaymentInfo(command.paymentId(),
			command.requestMemberId());

		// 2. paymentId로 이미 충전됐는지 확인한다.
		// 포인트 중복 충전 방지
		if (pointChargedLogRepository.findByPaymentIdAndCharged(command.paymentId(), true).isPresent()) {
			log.warn("[PointPurchaseService.confirm] already processed payment - paymentId: {}, requestMemberId: {}",
				command.paymentId(), command.requestMemberId());
			pointChargedLogRepository.save(
				PointChargedLog.failed(command.paymentId(), paymentInfo.totalPaidAmount(), paymentInfo.pointAmount(),
					command.requestMemberId(), "이미 사용된 결제"));
			throw new PointPurchaseException("이미 사용된 결제입니다.");
		}

		// 3. 결재 금액이랑 대조
		// 잘못된 결제 금액을 통한 포인트 충전 방지
		final PointProduct pointProduct = pointProductRepository.findById(paymentInfo.paymentProductId())
			.orElseThrow(() -> {
				log.info("[PointPurchaseService.confirm] not found point product - paymentId: {}, productId: {}",
					command.paymentId(), paymentInfo.paymentProductId());
				pointChargedLogRepository.save(
					PointChargedLog.failed(command.paymentId(), paymentInfo.totalPaidAmount(),
						paymentInfo.pointAmount(),
						command.requestMemberId(), "상품을 찾을 수 없습니다."));
				return new PointPurchaseException("상품을 찾을 수 없습니다.");
			});
		// 상품 금액이 실제 결제 금액이랑 다른 경우
		if (!paymentInfo.totalPaidAmount().equals(pointProduct.getPrice().longValue())) {
			log.warn(
				"[PointPurchaseService.confirm] price is not match - paymentId: {}, productId: {}, paidAmount: {}, realAmount: {}",
				command.paymentId(), command.requestMemberId(), paymentInfo.totalPaidAmount(),
				pointProduct.getPrice().longValue());
			pointChargedLogRepository.save(
				PointChargedLog.failed(command.paymentId(), paymentInfo.totalPaidAmount(), paymentInfo.pointAmount(),
					command.requestMemberId(), "결제 금액이랑 상품 가격이 다릅니다."));
			throw new PointPurchaseException("결제 금액이랑 상품 가격이 다릅니다.");
		}

		// 4. 결제 사용자랑 다르면 에러
		// mim 방지
		final Long customerId = paymentInfo.customerId();
		if (!customerId.equals(command.requestMemberId())) {
			log.warn(
				"[PointPurchaseService.confirm] another member tried to login with others purchase - paymentId: {}, paidMemberId: {}, requestMemberId: {}",
				command.paymentId(), customerId, command.requestMemberId());
			pointChargedLogRepository.save(
				PointChargedLog.failed(command.paymentId(), paymentInfo.totalPaidAmount(), paymentInfo.pointAmount(),
					command.requestMemberId(), "다른 사람의 결제로 포인트 충전을 시도했습니다."));
			throw new PointPurchaseException("다른 사람의 결제로 포인트 충전을 시도했습니다.");
		}

		// 5. 포인트 충전 금액이랑 결제 할때 예상한 포인트 충전 금액이랑 다르면 예외
		final Long pointAmount = paymentInfo.pointAmount();
		if (!pointAmount.equals(pointProduct.getPointAmount().longValue())) {
			pointChargedLogRepository.save(
				PointChargedLog.failed(command.paymentId(), paymentInfo.totalPaidAmount(), paymentInfo.pointAmount(),
					command.requestMemberId(), "결제시 포인트 충전 예상량과, 실제 충전액이 다릅니다. 관리자에게 문의해주세요"));
			throw new PointPurchaseException("결제시 포인트 충전 예상량과, 실제 충전액이 다릅니다. 관리자에게 문의해주세요");
		}

		// 6. success -> 포인트 충전
		pointChargedLogRepository.save(
			PointChargedLog.succeeded(command.paymentId(), paymentInfo.totalPaidAmount(), pointAmount, customerId));
		final UserPoint userPoint = userPointRepository.findByUserId(customerId)
			.orElseGet(() -> userPointRepository.save(UserPoint.of(customerId)));
		userPoint.addPoint(pointAmount);
	}
}
