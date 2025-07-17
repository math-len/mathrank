package kr.co.mathrank.app.api.common.log;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;

@Configuration
class MDCConfiguration {
	@Bean
	public FilterRegistrationBean<MDCInsertingServletFilter> mdcFilter() {
		FilterRegistrationBean<MDCInsertingServletFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(mdcInsertingServletFilter());
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 가장 먼저 실행
		registration.addUrlPatterns("/*"); // 모든 요청에 적용
		return registration;
	}

	@Bean
	MDCInsertingServletFilter mdcInsertingServletFilter() {
		return new MDCInsertingServletFilter();
	}
}
