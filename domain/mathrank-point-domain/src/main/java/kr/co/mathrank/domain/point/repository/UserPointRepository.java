package kr.co.mathrank.domain.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.point.entity.UserPoint;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {
	@Lock(LockModeType.OPTIMISTIC)
	Optional<UserPoint> findByUserId(Long userId);
}
