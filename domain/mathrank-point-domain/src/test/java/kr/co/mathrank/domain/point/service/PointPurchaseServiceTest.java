package kr.co.mathrank.domain.point.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import io.portone.sdk.server.payment.PaymentClient;
import kr.co.mathrank.domain.point.dto.PointPurchaseCommand;
import kr.co.mathrank.domain.point.entity.Currency;
import kr.co.mathrank.domain.point.entity.PointProduct;
import kr.co.mathrank.domain.point.exception.PointPurchaseException;
import kr.co.mathrank.domain.point.repository.PointChargedLogRepository;
import kr.co.mathrank.domain.point.repository.PointProductRepository;
import kr.co.mathrank.domain.point.repository.UserPointRepository;

@SpringBootTest
class PointPurchaseServiceTest {
	@MockitoBean
	private PaymentClient paymentClient;
	@MockitoBean
	private PointProductRepository pointProductRepository;
	@Autowired
	private PointPurchaseService pointPurchaseService;
	@Autowired
	private UserPointRepository userPointRepository;
	@MockitoBean
	private PortOnePaymentClient portOnePaymentClient;
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private PointChargedLogRepository pointChargedLogRepository;

	@Test
	void 동시_충전시_하나만_적용된다() throws InterruptedException {
		final String paymentId = "1";
		final Long customerId = 2L;
		final Long paymentProductId = 3L;
		final Long productPrice = 1000L;
		final Long pointAmount = 5000L;

		final PortOnePaymentClient.PaymentInfo paymentInfo = mockPaidPayment(productPrice, customerId, paymentProductId,
			pointAmount);

		// 클라이언트 호출시 위 객체 반환
		Mockito.when(portOnePaymentClient.getPaymentInfo(Mockito.anyString(), Mockito.anyLong()))
			.thenReturn(paymentInfo);

		// 상품 조회 시, 위에서 정의한 스펙 반환
		Mockito.when(pointProductRepository.findById(Mockito.anyLong()))
			.thenReturn(
				Optional.of(
					PointProduct.of(BigDecimal.valueOf(pointAmount), Currency.KRW, BigDecimal.valueOf(productPrice))));

		final ExecutorService executorService = Executors.newFixedThreadPool(5);
		final int tryCount = 20;
		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		for (int i = 0; i < tryCount; i++) {
			executorService.submit(() -> {
				try {
					pointPurchaseService.confirm(new PointPurchaseCommand(paymentId, customerId));
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		// 포인트는 한번만 충전된다
		transactionTemplate.execute(status -> {
			Assertions.assertEquals(userPointRepository.findByUserId(customerId).get().getPointAmount(), pointAmount);
			return null;
		});
	}

	@Test
	void 다른_아이디로_충전_시도하면_예외발생() {
		final String paymentId = "1";
		// 사용자
		final Long customerId = 2L; // 실제 결제한 사람
		final Long anotherCustomerId = customerId + 1; // 결제 안하고 paymentId 훔친 사람

		final Long paymentProductId = 3L;
		final Long productPrice = 1000L;
		final Long pointAmount = 5000L;

		final PortOnePaymentClient.PaymentInfo paymentInfo = mockPaidPayment(productPrice, customerId, paymentProductId,
			pointAmount);

		// 클라이언트 호출시 위 객체 반환
		Mockito.when(portOnePaymentClient.getPaymentInfo(Mockito.anyString(), Mockito.anyLong()))
			.thenReturn(paymentInfo);

		// 상품 조회 시, 위에서 정의한 스펙 반환
		Mockito.when(pointProductRepository.findById(Mockito.anyLong()))
			.thenReturn(
				Optional.of(
					PointProduct.of(BigDecimal.valueOf(pointAmount), Currency.KRW, BigDecimal.valueOf(productPrice))));

		Assertions.assertThrows(PointPurchaseException.class, () -> pointPurchaseService.confirm(new PointPurchaseCommand(paymentId, anotherCustomerId)));
	}

	@Test
	void 실제_결제_금액이랑_상품가격이_다르면_예외() {
		final String paymentId = "1";
		// 사용자
		final Long customerId = 2L; // 실제 결제한 사람

		final Long paymentProductId = 3L;
		final Long productPrice = 1000L;
		final Long paidAmount = 100L;
		final Long pointAmount = 5000L;

		final PortOnePaymentClient.PaymentInfo paymentInfo = mockPaidPayment(paidAmount, customerId, paymentProductId,
			pointAmount);

		// 클라이언트 호출시 위 객체 반환
		Mockito.when(portOnePaymentClient.getPaymentInfo(Mockito.anyString(), Mockito.anyLong()))
			.thenReturn(paymentInfo);

		// 상품 조회 시, 위에서 정의한 스펙 반환
		Mockito.when(pointProductRepository.findById(Mockito.anyLong()))
			.thenReturn(
				Optional.of(
					PointProduct.of(BigDecimal.valueOf(pointAmount), Currency.KRW, BigDecimal.valueOf(productPrice))));

		Assertions.assertThrows(PointPurchaseException.class, () -> pointPurchaseService.confirm(new PointPurchaseCommand(paymentId, customerId)));
	}

	@Test
	void 예상_충전금액과_실제_충전금액이_다르면_예외() {
		final String paymentId = "1";
		// 사용자
		final Long customerId = 2L; // 실제 결제한 사람
		final Long paymentProductId = 3L;
		final Long productPrice = 1000L;
		final Long pointAmount = 5000L;
		final Long realPointAmount = 3000L;

		final PortOnePaymentClient.PaymentInfo paymentInfo = mockPaidPayment(productPrice, customerId, paymentProductId,
			pointAmount);

		// 클라이언트 호출시 위 객체 반환
		Mockito.when(portOnePaymentClient.getPaymentInfo(Mockito.anyString(), Mockito.anyLong()))
			.thenReturn(paymentInfo);

		// 상품 조회 시, 위에서 정의한 스펙 반환
		Mockito.when(pointProductRepository.findById(Mockito.anyLong()))
			.thenReturn(
				Optional.of(
					PointProduct.of(BigDecimal.valueOf(realPointAmount), Currency.KRW, BigDecimal.valueOf(productPrice))));

		Assertions.assertThrows(PointPurchaseException.class, () -> pointPurchaseService.confirm(new PointPurchaseCommand(paymentId, customerId)));
	}

	@AfterEach
	void clear() {
		userPointRepository.deleteAll();
		pointChargedLogRepository.deleteAll();
	}

	private PortOnePaymentClient.PaymentInfo mockPaidPayment(final Long paidTotalAmount, final Long customerId,
		final Long paymentProductId, final Long pointAmount) {
		return new PortOnePaymentClient.PaymentInfo(paymentProductId, paidTotalAmount, pointAmount, customerId);
	}
}
