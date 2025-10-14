package kr.co.mathrank.domain.point.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.point.entity.PointChargedLog;

public interface PointChargedLogRepository extends JpaRepository<PointChargedLog, Long> {
	@Lock(LockModeType.PESSIMISTIC_READ)
	List<PointChargedLog> findAllByPaymentIdAndCharged(String paymentId, Boolean charged);
}
