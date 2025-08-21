package kr.co.mathrank.domain.problem.single.read.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemSolver;

public interface SingleProblemSolverRepository extends JpaRepository<SingleProblemSolver, Long> {
	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT sps FROM SingleProblemSolver sps WHERE sps.id = :singleProblemId AND sps.memberId = :memberId")
	Optional<SingleProblemSolver> findSolverForShare(@Param("memberId") Long memberId,
		@Param("singleProblemId") Long singleProblemId);
}
