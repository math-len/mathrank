package kr.co.mathrank.domain.problem.single.read;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.common.cache.RequiredCacheSpec;

@Configuration
public class SingleProblemReadModelConfiguration {
	public static final String COURSE_CACHE = "mathrank::domain::single-problem::read::course";
	public static final String MATHRANK_SINGLE_PROBLEM_PAGE_CACHE = "mathrank::domain::single-problem::page";
	public static final String MATHRANK_SINGLE_PROBLEM_SINGLE_CACHE = "mathrank::domain::single-problem::single";
	public static final String MATHRANK_SINGLE_PROBLEM_MY_CACHE = "mathrank::domain::single-problem::my";

	@Bean
	RequiredCacheSpec courseCacheSpec() {
		return cacheSpec(COURSE_CACHE, Duration.ofSeconds(30));
	}

	private RequiredCacheSpec cacheSpec(final String cacheName, final Duration ttl) {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "single-problem-read-model-domain";
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

	@Bean
	RequiredCacheSpec singleProblemPageCacheSpec() {
		return cacheSpec(MATHRANK_SINGLE_PROBLEM_PAGE_CACHE, Duration.ofSeconds(2));
	}

	@Bean
	RequiredCacheSpec singleProblemSingleCacheSpec() {
		return cacheSpec(MATHRANK_SINGLE_PROBLEM_SINGLE_CACHE, Duration.ofSeconds(2));
	}

	@Bean
	RequiredCacheSpec singleProblemSingleCacheSpec() {
		return cacheSpec(MATHRANK_SINGLE_PROBLEM_MY_CACHE, Duration.ofSeconds(5));
	}
}
