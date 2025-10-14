package kr.co.mathrank.domain.point.service;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.dataserializer.DataSerializer;
import kr.co.mathrank.domain.point.entity.PointChargedLog;
import kr.co.mathrank.domain.point.exception.PointPurchaseException;
import kr.co.mathrank.domain.point.repository.PointChargedLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class PortOnePaymentClient {
	private final PaymentClient paymentClient;
	private final PointChargedLogRepository pointChargedLogRepository;

	public PaymentInfo getPaymentInfo(@NotNull final String paymentId, @NotNull final Long requestMemberId) {
		final PaidPayment paidPayment = fetchSucceededPayment(paymentId);
		final Long paymentTotalAmount = paidPayment.getAmount().getTotal();
		final PaymentCustomDataPayload paymentCustomPayload = DataSerializer.deserialize(paidPayment.getCustomData(), PaymentCustomDataPayload.class)
			.orElseThrow(() -> {
				log.warn("[PointPurchaseService.confirm] payment custom data payload is invalid - payload: {}", paidPayment.getCustomData());
				pointChargedLogRepository.save(PointChargedLog.failed(paymentId, paymentTotalAmount, null,
					requestMemberId, "customData 형식이 잘못됐습니다."));
				return new PointPurchaseException("customData 형식이 잘못됐습니다.");
			});

		return new PaymentInfo(
			paymentCustomPayload.paymentProductId(),
			paymentTotalAmount,
			paymentCustomPayload.pointAmount(),
			Long.parseLong(paidPayment.getCustomer().getId())
		);
	}


	private PaidPayment fetchSucceededPayment(final String paymentId) {
		final Payment payment = paymentClient.getPayment(paymentId).join();

		// 성공한 결제가 아닐때 실패 처리
		if (!(payment instanceof PaidPayment)) {
			log.warn("[PointPurchaseService.confirm] not paid payment - paymentId: {}", paymentId);
			throw new PointPurchaseException("실패한 결제 입니다. - paymentId: " + paymentId);
		}

		return (PaidPayment)payment;
	}

	record PaymentInfo(
		Long paymentProductId, // paymentId
		Long totalPaidAmount, // 총 결제 금액
		Long pointAmount, // 충전 예상 포인트 양
		Long customerId // 사용자 ID
	) {
	}

	record PaymentCustomDataPayload(
		Long paymentProductId, // 상품 id
		Long pointAmount // 구매한 point 양
	) {
	}
}
