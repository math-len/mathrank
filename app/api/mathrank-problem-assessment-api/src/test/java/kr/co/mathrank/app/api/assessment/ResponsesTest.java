package kr.co.mathrank.app.api.assessment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponsesTest {
	@Test
	@DisplayName("시험지 등록 응답은 QR 생성에 사용할 assessmentId를 문자열로 반환한다")
	void assessmentRegisterResponse() {
		final Responses.AssessmentRegisterResponse response =
			Responses.AssessmentRegisterResponse.from(123L);

		assertThat(response.assessmentId()).isEqualTo("123");
	}
}
