package kr.co.mathrank.domain.rank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.rank.entity.Solver;

public interface SolverRepository extends JpaRepository<Solver, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Solver s WHERE s.memberId = :memberId")
	Optional<Solver> findByMemberIdForUpdate(@Param("memberId") Long memberId);

	@Query("SELECT s FROM Solver s LEFT JOIN FETCH s.solveLogs WHERE s.memberId = :memberId")
	Optional<Solver> findByMemberId(Long memberId);
}
