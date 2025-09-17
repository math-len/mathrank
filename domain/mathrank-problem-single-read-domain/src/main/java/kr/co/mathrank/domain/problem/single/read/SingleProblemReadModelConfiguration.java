package kr.co.mathrank.domain.problem.single.read;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.common.cache.RequiredCacheSpec;

@Configuration
public class SingleProblemReadModelConfiguration {
	public static final String COURSE_CACHE = "mathrank::domain::single-problem::read::course";

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
}
