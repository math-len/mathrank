package kr.co.mathrank.domain.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.point.entity.UserPoint;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {
	@Lock(LockModeType.OPTIMISTIC)
	Optional<UserPoint> findByUserId(Long userId);

	@Query("""
SELECT u FROM UserPoint u
WHERE u.userId = :memberId
""")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<UserPoint> findByUserIdForUpdate(@Param("memberId") Long memberId);
}
