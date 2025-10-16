package kr.co.mathrank.client.config;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import kr.co.mathrank.client.result.ClientResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestClientResponseDecorator {
	public <T> ClientResponse<T> wrap(final Supplier<T> supplier) {
		try {
			return new ClientResponse.Success<>(supplier.get());
		} catch (RestClientResponseException e) {
			log.warn("[RestClientResponseDecorator.wrap] http response exception - message: {}",
				e.getMessage(), e);
			return new ClientResponse.StatusCodeFailure<>(e.getStatusCode().value(), e.getResponseBodyAsString());
		} catch (ResourceAccessException e) {
			log.warn("[RestClientResponseDecorator.wrap] resource access timeout exception: {}", e.getMessage(), e);
			return new ClientResponse.TimeOutFailure<>(e.getMessage());
		}
	}
}
