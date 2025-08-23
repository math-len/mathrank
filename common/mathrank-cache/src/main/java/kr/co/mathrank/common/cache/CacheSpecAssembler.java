package kr.co.mathrank.common.cache;

import java.util.List;

import org.springframework.cache.CacheManager;

/**
 * 여러 모듈에서 제공한 {@link RequiredCacheSpec} 들을 조립하여
 * 최종적으로 사용할 {@link CacheManager} 인스턴스를 생성하는 계약(Assembler) 인터페이스입니다.
 *
 * <p>구현체는 환경에 따라 다른 CacheManager를 구성할 수 있습니다.
 * 예를 들어,
 * <ul>
 *   <li>모놀리식 환경: {@code CaffeineCacheManager} 기반으로 엔트리 수/TTL을 설정</li>
 *   <li>MSA 환경: {@code RedisCacheManager} 기반으로 캐시별 TTL/Serializer를 설정</li>
 * </ul>
 *
 * <p>전역 구성 클래스에서 모든 모듈의 {@code RequiredCacheSpec} 리스트를 수집해
 * 이 인터페이스를 구현한 Assembler에게 전달하면,
 * 중앙 CacheManager를 생성하고 Spring 컨텍스트에 빈으로 등록할 수 있습니다.
 */
public interface CacheSpecAssembler {

	/**
	 * 주어진 모듈별 필수 캐시 스펙 목록을 기반으로 CacheManager를 조립하여 반환합니다.
	 *
	 * @param requiredCacheSpecs 각 모듈이 정의한 캐시 이름과 TTL 등의 사양
	 * @return 조립된 CacheManager (Caffeine, Redis 등 구체 구현체)
	 */
	public abstract CacheManager assemble(List<RequiredCacheSpec> requiredCacheSpecs);
}
