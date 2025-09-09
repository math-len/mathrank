package kr.co.mathrank.common.cache.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.testcontainers.junit.jupiter.Testcontainers;

import kr.co.mathrank.common.cache.RequiredCacheSpec;

@Component
@Testcontainers
public class TestService {

	@Cacheable(cacheNames = "common::cache::redis::test", key = "#uuid")
	public String getString(int uuid) {
		return "test";
	}

	@Cacheable(cacheNames = "common::cache::redis::test", key = "'abstract::' + #uuid")
	public List<String> getAbstractValue(int uuid) {
		return List.of("test");
	}

	@Cacheable(cacheNames = "common::cache::redis::test", key = "'generic' +#uuid")
	public TestResult<String> getGenericResult(int uuid) {
		return new TestResult<>("test");
	}

	@Bean
	public RequiredCacheSpec requiredCacheSpec() {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "test-module";
			}

			@Override
			public String cacheName() {
				return "common::cache::redis::test";
			}

			@Override
			public Duration ttl() {
				return Duration.ofSeconds(1);
			}
		};
	}

	public record TestResult<T> (
		T result
	) {
	}
}
