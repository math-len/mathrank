package kr.co.mathrank.client.exception.aspect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.client.exception.ClientException;

@SpringBootTest
class ClientExceptionAspectTest {
	@Autowired
	private TestClient testClient;

	@Test
	void 어노테이션이_적용된_클라이언트가_던지는_예외는_커스텀_예외로_변환된다() {
		Assertions.assertThrows(ClientException.class, () -> testClient.execute());
	}
}