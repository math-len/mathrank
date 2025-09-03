package kr.co.mathrank.client.exception.aspect;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

@Component
@Client
public class TestClient {
	public void execute() {
		throw new HttpClientErrorException(HttpStatusCode.valueOf(400));
	}
}
