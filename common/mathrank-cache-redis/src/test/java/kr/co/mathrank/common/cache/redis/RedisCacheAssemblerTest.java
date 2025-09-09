package kr.co.mathrank.common.cache.redis;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class RedisCacheAssemblerTest {
	private static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.0.2")
		.withExposedPorts(6379);

	@DynamicPropertySource
	static void init(DynamicPropertyRegistry registry) {
		redisContainer.start();
		registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
		registry.add("spring.data.redis.host", () -> redisContainer.getHost());
	}

	@MockitoSpyBean
	TestService testService;

	@Test
	void 인터페이스_타입_역직렬화_시_예외_발생() {
		final int uuid = 1;
		// 첫 번째 호출
		testService.getAbstractValue(uuid);

		// 두 번째 호출 (캐시에서 값을 가져오는지 확인)
		Assertions.assertThatThrownBy(() -> testService.getAbstractValue(uuid));
	}

	@Test
	void 제네릭_타입_캐시_역직렬화_성공한다() {
		final int uuid = 1;
		// 제네릭 타입 결과 테스트
		TestService.TestResult<String> firstResult = testService.getGenericResult(uuid);
		TestService.TestResult<String> secondResult = testService.getGenericResult(uuid);

		// 결과가 동일한지 확인
		assertThat(firstResult.result()).isEqualTo("test");
		assertThat(secondResult.result()).isEqualTo("test");

		// 캐시된 값을 확인
		assertThat(firstResult).isEqualTo(secondResult);
	}

	@Test
	void 캐시적용으로_한번만_호출된다() {
		final int uuid = 10;

		testService.getGenericResult(uuid);
		testService.getGenericResult(uuid);

		Mockito.verify(testService, Mockito.times(1)).getGenericResult(uuid);
	}

	@Test
	void TTL_만료되면_다시호출한다() {
		final int uuid = 11;

		testService.getGenericResult(uuid);

		try {
			Thread.sleep(1500L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		testService.getGenericResult(uuid);

		Mockito.verify(testService, Mockito.times(2)).getGenericResult(uuid);
	}
}
