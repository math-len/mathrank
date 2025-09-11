package kr.co.mathrank.client.exception.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import kr.co.mathrank.client.exception.ClientBadRequestException;
import kr.co.mathrank.client.exception.ClientException;
import kr.co.mathrank.client.exception.ClientRequestTimeoutException;
import kr.co.mathrank.client.exception.ClientServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * 특정 모듈 내에서 발생하는 RestTemplate 기반 예외를
 * 공통적으로 잡아 도메인 맞춤형 예외(ClientBadRequestException, ClientServerException, ClientRequestTimeoutException 등)로 변환하는 Aspect 클래스
 */
@Aspect
@Slf4j
@Component
public class ClientExceptionAspect {
	@AfterThrowing(value = "@within(Client)", throwing = "throwable")
	public void mapException(final Throwable throwable) {
		// http 응답이 200번대가 아닐때
		if (throwable instanceof HttpStatusCodeException httpStatusCodeException) {
			log.error("HTTP Status Code: {}", httpStatusCodeException.getStatusCode(), httpStatusCodeException);
			// 400 번대 ( 잘못된 요청인 경우 )
			if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
				log.warn("[ClientExceptionAspect.mapException] wrong response with 4xx status code", httpStatusCodeException);
				throw new ClientBadRequestException(
					httpStatusCodeException.getMessage(),
					httpStatusCodeException.getCause()
				);
				// 500 번대 ( 서버 에러인 경우 )
			} else if (httpStatusCodeException.getStatusCode().is5xxServerError()) {
				log.error("[ClientExceptionAspect.mapException] wrong response with 5xx status code", httpStatusCodeException);
				throw new ClientServerException(
					httpStatusCodeException.getMessage(),
					httpStatusCodeException.getCause()
				);
			}
		}

		// 타임 아웃 에러인 경우
		if (throwable instanceof ResourceAccessException) {
			log.warn("Resource access timeout exception: ", throwable);
			throw new ClientRequestTimeoutException(throwable.getMessage(), throwable);
		}

		// 그 외 전체 래핑
		throw new ClientException(throwable.getMessage(), throwable);
	}
}
