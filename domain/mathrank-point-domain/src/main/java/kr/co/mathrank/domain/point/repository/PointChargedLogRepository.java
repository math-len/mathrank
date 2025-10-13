package kr.co.mathrank.domain.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.point.entity.PointChargedLog;

public interface PointChargedLogRepository extends JpaRepository<PointChargedLog, Long> {
	/**
	 * 최신 커밋 읽기
	 * @param paymentId
	 * @param charged
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_READ)
	Optional<PointChargedLog> findByPaymentIdAndCharged(String paymentId, Boolean charged);
}
