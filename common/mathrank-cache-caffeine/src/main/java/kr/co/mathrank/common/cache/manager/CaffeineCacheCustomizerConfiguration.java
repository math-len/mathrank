package kr.co.mathrank.common.cache.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import kr.co.mathrank.common.cache.CacheSpecAssembler;
import kr.co.mathrank.common.cache.RequiredCacheSpec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@Configuration
public class CaffeineCacheCustomizerConfiguration implements CacheSpecAssembler {
	@Value("${caffeine.cache.entry.count}")
	private Long cacheEntryCount;

	@Override
	@Bean
	public CacheManager assemble(List<RequiredCacheSpec> requiredCacheSpecs) {
		final CaffeineCacheManager cacheManager = new CaffeineCacheManager();
		for (final RequiredCacheSpec cacheSpec : requiredCacheSpecs) {
			final Cache<Object, Object> cache = Caffeine.newBuilder()
				.maximumSize(cacheEntryCount)
				.expireAfterWrite(cacheSpec.ttl()).build();
			cacheManager.registerCustomCache(cacheSpec.cacheName(), cache);
		}

		// 한 줄 로그로 요약
		log.info("[CaffeineCacheCustomizerConfiguration.assemble]: registered cache specs -> {}",
			requiredCacheSpecs.stream()
				.map(spec ->
					String.format("{module=%s, name=%s, ttl=%s}",
						spec.moduleName(),
						spec.cacheName(),
						spec.ttl()))
				.toList()
		);

		return cacheManager;
	}
}
