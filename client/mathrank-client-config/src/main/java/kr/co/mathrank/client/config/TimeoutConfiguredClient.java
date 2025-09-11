package kr.co.mathrank.client.config;

import java.time.Duration;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public abstract class TimeoutConfiguredClient {
	protected ClientHttpRequestFactory configureTimeoutConfiguration(final Duration connectionTimeout, final Duration readTimeout) {
		final SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(connectionTimeout);
		clientHttpRequestFactory.setReadTimeout(readTimeout);
		return clientHttpRequestFactory;
	}
}
