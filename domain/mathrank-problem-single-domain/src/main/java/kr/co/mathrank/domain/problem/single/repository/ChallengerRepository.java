package kr.co.mathrank.domain.problem.single.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.single.dto.ChallengerQueryResult;
import kr.co.mathrank.domain.problem.single.entity.Challenger;

public interface ChallengerRepository extends JpaRepository<Challenger, Long> {
	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT c FROM Challenger c LEFT JOIN FETCH c.challengeLogs WHERE c.singleProblem.id = :singleProblemId AND c.memberId = :memberId")
	Optional<Challenger> findByMemberIdAndSingleProblemIdForShare(@Param("memberId") Long memberId, @Param("singleProblemId") Long singleProblemId);

	@Query("SELECT c FROM Challenger c LEFT JOIN FETCH c.challengeLogs WHERE c.singleProblem.id = :singleProblemId AND c.memberId = :memberId")
	Optional<Challenger> findByMemberIdAndSingleProblemId(@Param("memberId") Long memberId, @Param("singleProblemId") Long singleProblemId);

	@Query("SELECT new kr.co.mathrank.domain.problem.single.dto.ChallengerQueryResult(c.singleProblem.id, c.successAtFirstTry) FROM Challenger c WHERE c.memberId = :memberId")
	List<ChallengerQueryResult> findByMemberId(@Param("memberId") Long memberId);
}
