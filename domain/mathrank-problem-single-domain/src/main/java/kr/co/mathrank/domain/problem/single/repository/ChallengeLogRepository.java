package kr.co.mathrank.domain.problem.single.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;

public interface ChallengeLogRepository extends JpaRepository<ChallengeLog, Long> {
	@Query(
"""
SELECT cl FROM ChallengeLog cl
LEFT JOIN FETCH cl.challenger c
LEFT JOIN FETCH c.singleProblem
WHERE cl.id = :challengeLogId
""")
	Optional<ChallengeLog> findWithSingleProblem(@Param("challengeLogId") Long challengeLogId);
}
