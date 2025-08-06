package kr.co.mathrank.domain.problem.single.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;

public interface ChallengeLogRepository extends JpaRepository<ChallengeLog, Long> {
	List<ChallengeLog> findAllBySingleProblemId(Long problemId);

	@Query("SELECT cl FROM ChallengeLog cl WHERE cl.singleProblem.id = :singleProblemId AND cl.memberId = :challengerMemberId")
	@Lock(LockModeType.PESSIMISTIC_READ)
	List<ChallengeLog> findAllBySingleProblemIdAndChallengerMemberIdForShare(@Param("singleProblemId") Long singleProblemId, @Param("challengerMemberId") Long challengerMemberId);
}
