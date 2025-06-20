package kr.co.mathrank.common.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kr.co.mathrank.common.exception.ExceptionMessage;
import kr.co.mathrank.common.exception.MathRankException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

class MathRankExceptionTest {

	@Test
	void 커스텀_메세지로_처리하는_예외_테스트() {
		assertThatThrownBy(() -> {
			throw new CustomException(TestException.TEST_MESSAGE);
		})
			.isInstanceOf(MathRankException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", TestException.TEST_MESSAGE);
	}

	@Test
	void 커스텀_예외_메세지_처리와_Throwable_전파_예외_테스트() {
		// given
		Throwable cause = new Throwable("test");

		// when & then
		assertThatThrownBy(() -> {
			throw new CustomException(TestException.TEST_MESSAGE, cause);
		})
			.isInstanceOf(MathRankException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", TestException.TEST_MESSAGE)
			.hasMessageContaining("test");
	}

	@Test
	void String_예외_처리_테스트() {
		// given
		// when & then
		assertThatThrownBy(() -> {
			throw new CustomException("TEST");
		})
			.isInstanceOf(MathRankException.class)
			.satisfies(exception -> {
				MathRankException ex = (MathRankException) exception;
				assertThat(ex.getExceptionMessage().getMessage()).isEqualTo("TEST");
			});
	}

	class CustomException extends MathRankException {
		public CustomException(ExceptionMessage message) {
			super(message);
		}
		public CustomException(ExceptionMessage message, Throwable cause) {
			super(message, cause);
		}
		public CustomException(String message) {
			super(message);
		}
	}


	@RequiredArgsConstructor
	@Getter
	enum TestException implements ExceptionMessage {
		TEST_MESSAGE("test message");

		private final String message;
	}
}