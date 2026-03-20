package kr.co.mathrank.domain.rank.service;

import kr.co.mathrank.client.internal.member.MemberClient;
import kr.co.mathrank.domain.rank.entity.SchoolScore;
import kr.co.mathrank.domain.rank.repository.SchoolScoreRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	private final SchoolRankScoreManager schoolRankScoreManager;
	private final ProblemClient problemClient;
	private final MemberClient memberClient;

	private final SolveLogSaveManager solveLogSaveManager;

	@CacheEvict(key = "#command.memberId()")
	@Transactional
	public void register(@NotNull @Valid final SolveLogRegisterCommand command) {
		final Integer score = getProblemScore(command.problemId());
		solveLogSaveManager.save(command, score);

		final String schoolCode = memberClient.getMemberInfo(command.memberId()).schoolCode();
		schoolRankScoreManager.addSchoolScore(schoolCode, score);
	}

	private int getProblemScore(final Long problemId) {
		final ProblemQueryResult result = problemClient.fetchProblemInfo(problemId);
		return rankScoreManager.mapToScore(result.difficulty());
	}
}
