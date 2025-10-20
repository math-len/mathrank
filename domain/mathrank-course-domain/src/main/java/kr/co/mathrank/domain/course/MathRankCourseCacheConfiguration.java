package kr.co.mathrank.domain.course;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.mathrank.common.cache.RequiredCacheSpec;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MathRankCourseCacheConfiguration {
	public static final String MATHRANK_COURSE_PARENT_CACHE = "mathrank::course::parents::cache";
	public static final String MATHRANK_COURSE_CHILD_CACHE = "mathrank::course::childes::cache";

	@Bean
	RequiredCacheSpec mathRankCourseParentCacheSpec() {
		return createCacheSpec(MATHRANK_COURSE_PARENT_CACHE, Duration.ofMinutes(1L));
	}

	@Bean
	RequiredCacheSpec mathRankCourseChildesCacheSpec() {
		return createCacheSpec(MATHRANK_COURSE_CHILD_CACHE, Duration.ofMinutes(1L));
	}

	private RequiredCacheSpec createCacheSpec(final String cacheName, final Duration ttl) {
		return new RequiredCacheSpec() {
			@Override
			public String moduleName() {
				return "mathrank-course-domain";
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
