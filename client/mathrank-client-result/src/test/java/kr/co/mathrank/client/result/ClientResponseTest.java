package kr.co.mathrank.client.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClientResponseTest {
	@Test
	void 실패일때_예외던진다() {
		final ClientResponse<String> statusCodeFailure = new ClientResponse.StatusCodeFailure<>(111, "test");
		final ClientResponse<String> timeOutFailure = new ClientResponse.TimeOutFailure<>("test");

		Assertions.assertAll(
			() -> Assertions.assertThrows(ClientHttpStatusException.class, statusCodeFailure::getOrThrow),
			() -> Assertions.assertThrows(ClientTimeoutException.class, timeOutFailure::getOrThrow)
		);
	}

	@Test
	void 예외일때_디폴트_값_설정시_디폴트로_응답() {
		final ClientResponse<String> statusCodeFailure = new ClientResponse.StatusCodeFailure<>(111, "test");
		final ClientResponse<String> timeOutFailure = new ClientResponse.TimeOutFailure<>("test");

		Assertions.assertAll(
			() -> Assertions.assertThrows(ClientTimeoutException.class, timeOutFailure::getOrThrow),
			() -> Assertions.assertEquals("success", timeOutFailure.getOrDefault("success")),

			() -> Assertions.assertThrows(ClientHttpStatusException.class, statusCodeFailure::getOrThrow),
			() -> Assertions.assertEquals("success", statusCodeFailure.getOrDefault("success"))
		);
	}

	@Test
	void 성공시_optional_응답() {
		final ClientResponse<String> result = new ClientResponse.Success<>("");
		Assertions.assertTrue(result.get().isPresent());
	}

	@Test
	void 실패시_빈_optional_응답() {
		final ClientResponse<String> statusCodeFailure = new ClientResponse.StatusCodeFailure<>(111, "test");
		final ClientResponse<String> timeOutFailure = new ClientResponse.TimeOutFailure<>("test");

		Assertions.assertAll(
			() -> Assertions.assertTrue(statusCodeFailure.get().isEmpty()),
			() -> Assertions.assertTrue(timeOutFailure.get().isEmpty())
		);
	}
}
