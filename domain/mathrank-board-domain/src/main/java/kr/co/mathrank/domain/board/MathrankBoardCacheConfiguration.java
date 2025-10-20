package kr.co.mathrank.domain.board;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.common.cache.RequiredCacheSpec;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MathrankBoardCacheConfiguration {
	public static final String MATHRANK_POST_PAGE_CACHE = "mathrank::domain::post::page";
	public static final String MATHRANK_POST_SINGLE_CACHE = "mathrank::domain::post::single";

	@Bean
	RequiredCacheSpec mathRankPostPageCacheSpec() {
		return createCacheSpec(MATHRANK_POST_PAGE_CACHE, Duration.ofSeconds(3));
	}

	@Bean
	RequiredCacheSpec mathRankPostSingleCacheSpec() {
		return createCacheSpec(MATHRANK_POST_SINGLE_CACHE, Duration.ofSeconds(2));
	}

	private RequiredCacheSpec createCacheSpec(final String cacheName, final Duration ttl) {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "mathrank-board-domain";
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
