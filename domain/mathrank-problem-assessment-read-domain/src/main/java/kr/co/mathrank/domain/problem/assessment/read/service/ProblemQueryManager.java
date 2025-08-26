package kr.co.mathrank.domain.problem.assessment.read.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.assessment.read.AssessmentReadDomainConfiguration;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Validated
@CacheConfig(cacheNames = AssessmentReadDomainConfiguration.PROBLEM_CACHE_NAME)
class ProblemQueryManager {
	private final ProblemClient problemClient;

	@Cacheable(key = "#problemId")
	public ProblemQueryResult getProblemInfo(@NotNull final Long problemId) {
		return problemClient.fetchProblemInfo(problemId);
	}
}
