package kr.co.mathrank.domain.problem.assessment.read;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.common.cache.RequiredCacheSpec;

@Configuration
public class AssessmentReadDomainConfiguration {
	public static final String PROBLEM_CACHE_NAME = "mathrank::assessment-read-domain::problem";
	public static final String COURSE_CACHE_NAME = "mathrank::assessment-read-domain::course";
	public static final String ASSESSMENT_READ_MODEL_CACHE_NAME = "mathrank::assessment-read-domain::assessment";

	@Bean
	RequiredCacheSpec assessmentReadDomainProblemCacheSpec() { // problem cache 등록
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "assessment-read-domain";
			}

			@Override
			public String cacheName() {
				return PROBLEM_CACHE_NAME;
			}

			@Override
			public Duration ttl() {
				return Duration.ofSeconds(60);
			}
		};
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainCourseCacheSpec() { // problem cache 등록
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "assessment-read-domain";
			}

			@Override
			public String cacheName() {
				return COURSE_CACHE_NAME;
			}

			@Override
			public Duration ttl() {
				return Duration.ofSeconds(60);
			}
		};
	}

	@Bean
	RequiredCacheSpec assessmentReadDomainAssessmentCacheSpec() { // problem cache 등록
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "assessment-read-domain";
			}

			@Override
			public String cacheName() {
				return ASSESSMENT_READ_MODEL_CACHE_NAME;
			}

			@Override
			public Duration ttl() {
				return Duration.ofSeconds(60);
			}
		};
	}
}
