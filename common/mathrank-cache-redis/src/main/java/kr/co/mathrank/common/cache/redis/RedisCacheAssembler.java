package kr.co.mathrank.common.cache.redis;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import kr.co.mathrank.common.cache.CacheSpecAssembler;
import kr.co.mathrank.common.cache.RequiredCacheSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisCacheAssembler implements CacheSpecAssembler {
	private final RedisConnectionFactory redisConnectionFactory;

	@Bean
	@Override
	public CacheManager assemble(List<RequiredCacheSpec> requiredCacheSpecs) {
		final Map<String, RedisCacheConfiguration> cacheEntryConfigurations = mapToRedisCacheConfigurations(requiredCacheSpecs);
		return createCacheManager(cacheEntryConfigurations);
	}

	private CacheManager createCacheManager(
		Map<String, RedisCacheConfiguration> caches
	) {
		return RedisCacheManager.builder()
			.withInitialCacheConfigurations(caches)
			.cacheWriter(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
			.build();
	}

	private RedisCacheConfiguration serializeConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(
				RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()
					.configure(objectMapper -> objectMapper.registerModule(new JavaTimeModule()))));
	}

	/**
	 * {@link RequiredCacheSpec}을 {@link RedisCacheConfiguration} 로 변환해주는 메소드
	 * @param requiredCacheSpecs
	 * @return
	 */
	private Map<String, RedisCacheConfiguration> mapToRedisCacheConfigurations(
		final List<RequiredCacheSpec> requiredCacheSpecs
	) {
		return requiredCacheSpecs.stream()
			.collect(Collectors.toMap(
				RequiredCacheSpec::cacheName,
				requiredCacheSpec -> serializeConfiguration()
					.entryTtl(requiredCacheSpec.ttl())));
	}
}
