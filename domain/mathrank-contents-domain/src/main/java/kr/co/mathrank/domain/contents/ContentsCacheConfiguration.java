package kr.co.mathrank.domain.contents;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.common.cache.RequiredCacheSpec;

@Configuration
public class ContentsCacheConfiguration {
	public static final String CONTENT_ORDER_PAGE_CACHE_NAME = "mathrank-content-order-page";
	public static final String CONTENT_LIST_PAGE_CACHE_NAME = "mathrank-content-page";

	@Bean
	RequiredCacheSpec contentPageCache() {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "mathrank-content-domain";
			}

			@Override
			public String cacheName() {
				return CONTENT_LIST_PAGE_CACHE_NAME;
			}

			@Override
			public Duration ttl() {
				return Duration.ofSeconds(2);
			}
		};
	}

	@Bean
	RequiredCacheSpec contentOrderPageCache() {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "mathrank-content-domain";
			}

			@Override
			public String cacheName() {
				return CONTENT_ORDER_PAGE_CACHE_NAME;
			}

			@Override
			public Duration ttl() {
				return Duration.ofSeconds(5);
			}
		};
	}
}
