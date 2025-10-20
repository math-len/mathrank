package kr.co.mathrank.domain.problem.assessment;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.common.cache.RequiredCacheSpec;

@Configuration
public class AssessmentReadDomainConfiguration {
	public static final String PROBLEM_CACHE_NAME = "mathrank::assessment-domain::problem";
	public static final String COURSE_CACHE_NAME = "mathrank::assessment-domain::course";
	public static final String ASSESSMENT_READ_MODEL_CACHE_NAME = "mathrank::assessment-domain::assessment::read-model::single";
	public static final String ASSESSMENT_READ_SINGLE_CACHE = "mathrank::assessment-domain::assessment::single";
	public static final String ASSESSMENT_READ_PAGE_CAHCE = "mathrank::assessment-domain::assessment::page";
	public static final String ASSESSMENT_READ_RANK_CACHE = "mathrank::assessment-domain::assessment::rank";
	public static final String ASSESSMENT_READ_SOLUTION_CACHE = "mathrank::assessment-domain::assessment::solution";
	public static final String ASSESSMENT_STATISTICS_CACHE = "mathrank::assessment-domain::assessment::statistics";
	public static final String ASSESSMENT_SUBMISSION_SINGLE_CACHE = "mathrank::assessment-domain::assessment::submission::single";

	@Bean
	RequiredCacheSpec assessmentReadDomainProblemCacheSpec() {
		return createCacheSpec(PROBLEM_CACHE_NAME, Duration.ofSeconds(5L));
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainCourseCacheSpec() {
		return createCacheSpec(COURSE_CACHE_NAME, Duration.ofMinutes(1L));
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainAssessmentCacheSpec() {
		return createCacheSpec(ASSESSMENT_READ_MODEL_CACHE_NAME, Duration.ofSeconds(5L));
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainAssessmentSingleCacheSpec() {
		return createCacheSpec(ASSESSMENT_READ_SINGLE_CACHE, Duration.ofSeconds(5L));
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainAssessmentPageCacheSpec() {
		return createCacheSpec(ASSESSMENT_READ_PAGE_CAHCE, Duration.ofSeconds(3L));
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainAssessmentRankCacheSpec() {
		return createCacheSpec(ASSESSMENT_READ_RANK_CACHE, Duration.ofSeconds(3L));
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainAssessmentSolutionCacheSpec() {
		return createCacheSpec(ASSESSMENT_READ_SOLUTION_CACHE, Duration.ofSeconds(10L));
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainAssessmentStatisticsCacheSpec() {
		return createCacheSpec(ASSESSMENT_STATISTICS_CACHE, Duration.ofSeconds(10L));
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainAssessmentSingleSubmissionCacheSpec() {
		return createCacheSpec(ASSESSMENT_SUBMISSION_SINGLE_CACHE, Duration.ofMinutes(1L));
	}

	private RequiredCacheSpec createCacheSpec(final String cacheName, final Duration ttl) {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "assessment-read-domain";
			}

			@Override
			public String cacheName() {
				return cacheName;
			}

			@Override
			public Duration ttl() {
				return ttl;
			}
		};
	}
}
