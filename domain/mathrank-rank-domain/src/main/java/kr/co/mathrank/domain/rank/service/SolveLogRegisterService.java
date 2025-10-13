package kr.co.mathrank.domain.rank.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.rank.RankDomainConfiguration;
import kr.co.mathrank.domain.rank.dto.SolveLogRegisterCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@CacheConfig(cacheNames = RankDomainConfiguration.USER_RANK_CACHE_NAME)
public class SolveLogRegisterService {
	private final ProblemRankScoreManager rankScoreManager;
	private final ProblemClient problemClient;

	private final SolveLogSaveManager solveLogSaveManager;

	@CacheEvict(key = "#command.memberId()")
	public void register(@NotNull @Valid final SolveLogRegisterCommand command) {
		final Integer score = getProblemScore(command.problemId());
		solveLogSaveManager.save(command, score);
	}

	private int getProblemScore(final Long problemId) {
		final ProblemQueryResult result = problemClient.fetchProblemInfo(problemId);
		return rankScoreManager.mapToScore(result.difficulty());
	}
}
