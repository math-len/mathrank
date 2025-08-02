package kr.co.mathrank.domain.problem.single.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;

public interface ChallengeLogRepository extends JpaRepository<ChallengeLog, Long> {
	List<ChallengeLog> findAllBySingleProblemId(Long problemId);
}
