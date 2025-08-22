package kr.co.mathrank.domain.problem.single.read.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelResult;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;

public interface SingleProblemReadModelRepository
	extends JpaRepository<SingleProblemReadModel, Long>, SingleProblemReadModelQueryRepository {
	@Query("SELECT sp FROM SingleProblemReadModel sp WHERE sp.problemId = :problemId")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<SingleProblemReadModel> findByProblemIdForUpdate(@Param("problemId") Long problemId);

	@Query("SELECT sp FROM SingleProblemReadModel sp WHERE sp.id = :singleProblemId")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<SingleProblemReadModel> findByIdForUpdate(@Param("singleProblemId") Long singleProblemId);

	@Query(
"""
	SELECT new kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelResult(sp.id, slv.success, sp.problemId, sp.singleProblemName, sp.problemImage, sp.coursePath, sp.answerType, sp.difficulty, sp.firstTrySuccessCount, sp.totalAttemptedCount, sp.attemptedUserDistinctCount, sp.accuracy) 
	 FROM SingleProblemReadModel sp 
	 LEFT JOIN sp.solvers slv WITH slv.memberId = :memberId
	 WHERE sp.id = :singleProblemId
	""")
	Optional<SingleProblemReadModelResult> findByIdWithSolvedInfo(@Param("singleProblemId") Long singleProblemId, @Param("memberId") Long memberId);

	void deleteByProblemId(Long problemId);
}
