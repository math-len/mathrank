package kr.co.mathrank.domain.school;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.common.cache.RequiredCacheSpec;

@Configuration
class SchoolQueryCacheConfiguration {
	private static final String MODULE_NAME = "mathrank-domain-school";

	static final String SCHOOL_QUERY_BY_CITY_CACHE = "mathrank::domain::school::query::city";

	@Bean
	public RequiredCacheSpec schoolQueryByCityCache() {
		return createCacheSpec(SCHOOL_QUERY_BY_CITY_CACHE, Duration.ofMinutes(5L));
	}

	private RequiredCacheSpec createCacheSpec(final String cacheName, final Duration ttl) {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return MODULE_NAME;
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
