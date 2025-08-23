package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentRegisterException;

@SpringBootTest(properties = """
client.problem.port=20102
client.problem.host=http://localhost
""")
class AssessmentRegisterServiceTest {
	@Autowired
	private AssessmentRegisterService assessmentRegisterService;

	@Test
	void 문제_점수는_음수일_수_없다() {
		Assertions.assertThrows(
			ConstraintViolationException.class,
			() -> assessmentRegisterService.register(new AssessmentRegisterCommand(
				1L,
				Role.ADMIN,
				"testName",
				List.of(new AssessmentItemRegisterCommand(1L, -10)), // 점수가 음수
				Duration.ofMinutes(10L)
			))
		);
	}

	@Test
	void 총합이_100점이_아니면_예외() {
		Assertions.assertThrows(
			ConstraintViolationException.class,
			() -> assessmentRegisterService.register(new AssessmentRegisterCommand(
				1L,
				Role.ADMIN,
				"testName",
				List.of(new AssessmentItemRegisterCommand(1L, 10)), // 총합이 10점임
				Duration.ofMinutes(10L)
			))
		);
	}

	@Test
	void 총합이_100이면_통과() {
		Assertions.assertDoesNotThrow(
			() -> assessmentRegisterService.register(new AssessmentRegisterCommand(
				1L,
				Role.ADMIN,
				"testName",
				List.of(
					// 총합이 100점임
					new AssessmentItemRegisterCommand(1L, 50),
					new AssessmentItemRegisterCommand(1L, 50)
				),
				Duration.ofMinutes(10L)
			))
		);
	}

	@Test
	void 문제는_반드시_하나이상이여야_한다() {
		Assertions.assertThrows(
			ConstraintViolationException.class,
			() -> assessmentRegisterService.register(new AssessmentRegisterCommand(
				1L,
				Role.ADMIN,
				"testName",
				Collections.emptyList(),
				Duration.ofMinutes(10L)
			))
		);
	}

	@Test
	void 관리자가_아니면_시험지_생성_불가능하다() {
		Assertions.assertThrows(AssessmentRegisterException.class, () -> assessmentRegisterService.register(
			new AssessmentRegisterCommand(
				1L,
				Role.USER,
				"새로운 수학 문제집",
				List.of(new AssessmentItemRegisterCommand(101L, 33), new AssessmentItemRegisterCommand(102L, 33),
					new AssessmentItemRegisterCommand(103L, 34)),
				Duration.ofMinutes(100)
			))
		);
	}

	@DisplayName("register 메서드에 null을 전달하면 ConstraintViolationException이 발생한다.")
	@Test
	void whenRegisterWithNullCommand_thenThrowsException() {
		// when & then
		Assertions.assertThrows(ConstraintViolationException.class,
			() -> assessmentRegisterService.register(null));
	}

	@DisplayName("관리자(ADMIN) 권한으로 유효한 명령을 전달하면 성공적으로 등록된다.")
	@Test
	void whenRegisterWithValidCommandAsAdmin_thenSucceeds() {
		// given
		final AssessmentRegisterCommand validCommand = new AssessmentRegisterCommand(
			1L,
			Role.ADMIN,
			"새로운 수학 문제집",
			List.of(new AssessmentItemRegisterCommand(101L, 33),new AssessmentItemRegisterCommand(102L, 33), new AssessmentItemRegisterCommand(103L, 34)),
			Duration.ofMinutes(100)
		);

		// when & then
		Assertions.assertDoesNotThrow(() -> assessmentRegisterService.register(validCommand));
	}

	@DisplayName("유효하지 않은 AssessmentRegisterCommand로 등록을 시도하면 ConstraintViolationException이 발생한다.")
	@ParameterizedTest
	@MethodSource("invalidAssessmentRegisterCommands")
	void whenRegisterWithInvalidCommand_thenThrowsException(final AssessmentRegisterCommand invalidCommand) {
		// when & then
		Assertions.assertThrows(ConstraintViolationException.class,
			() -> assessmentRegisterService.register(invalidCommand));
	}

	private static Stream<Arguments> invalidAssessmentRegisterCommands() {
		return Stream.of(
			Arguments.of(new AssessmentRegisterCommand(null, Role.ADMIN, "유효한 이름", List.of(new AssessmentItemRegisterCommand(103L, 34)), Duration.ofMinutes(100))),
			// registerMemberId is null
			Arguments.of(new AssessmentRegisterCommand(1L, null, "유효한 이름", List.of(new AssessmentItemRegisterCommand(103L, 34)), Duration.ofMinutes(100))), // role is null
			Arguments.of(new AssessmentRegisterCommand(1L, Role.ADMIN, null, List.of(new AssessmentItemRegisterCommand(103L, 34)), Duration.ofMinutes(100))), // assessmentName is null
			Arguments.of(new AssessmentRegisterCommand(1L, Role.ADMIN, "", List.of(new AssessmentItemRegisterCommand(103L, 34)), Duration.ofMinutes(100))), // assessmentName is blank
			Arguments.of(new AssessmentRegisterCommand(1L, Role.ADMIN, "  ", List.of(new AssessmentItemRegisterCommand(103L, 34)), Duration.ofMinutes(100))), // assessmentName is blank
			Arguments.of(new AssessmentRegisterCommand(1L, Role.ADMIN, "유효한 이름", null, Duration.ofMinutes(100))), // problemIds is null
			Arguments.of(new AssessmentRegisterCommand(1L, Role.ADMIN, "유효한 이름", Collections.emptyList(), Duration.ofMinutes(100))),
			Arguments.of(new AssessmentRegisterCommand(1L, Role.ADMIN, "유효한 이름", List.of(new AssessmentItemRegisterCommand(103L, 34)), null))
			// problemIds is empty
		);
	}
}
