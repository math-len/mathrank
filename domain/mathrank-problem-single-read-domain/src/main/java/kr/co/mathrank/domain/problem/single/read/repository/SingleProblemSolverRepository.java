package kr.co.mathrank.domain.problem.single.read.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemSolver;

public interface SingleProblemSolverRepository extends JpaRepository<SingleProblemSolver, Long> {
	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT sps FROM SingleProblemSolver sps WHERE sps.singleProblemReadModel.id = :singleProblemId AND sps.memberId = :memberId")
	Optional<SingleProblemSolver> findSolverForShare(@Param("memberId") Long memberId,
		@Param("singleProblemId") Long singleProblemId);

	List<SingleProblemSolver> findByMemberIdAndSingleProblemReadModelIn(Long memberId, Collection<SingleProblemReadModel> singleProblemReadModels);

	List<SingleProblemSolver> findAllByMemberId(Long memberId);
}
