package kr.co.mathrank.domain.rank;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import kr.co.mathrank.common.cache.RequiredCacheSpec;

@EnableRetry
@Configuration
public class RankDomainConfiguration {
	public static final String USER_RANK_CACHE_NAME = "mathrank::domain::rank::user";
	public static final String RANK_BOARD_CACHE_NAME = "mathrank::domain::rank::board";

	@Bean
	RequiredCacheSpec userRankCacheSpec() {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "mathrank-domain-rank";
			}

			@Override
			public String cacheName() {
				return USER_RANK_CACHE_NAME;
			}

			@Override
			public Duration ttl() {
				return Duration.ofSeconds(10);
			}
		};
	}

	@Bean
	RequiredCacheSpec boardRankCacheSpec() {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "mathrank-domain-rank";
			}

			@Override
			public String cacheName() {
				return RANK_BOARD_CACHE_NAME;
			}

			@Override
			public Duration ttl() {
				return Duration.ofSeconds(5);
			}
		};
	}
}
