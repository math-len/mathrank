package kr.co.mathrank.domain.problem.single.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRegisterCommand;
import kr.co.mathrank.domain.problem.single.exception.AlreadyRegisteredProblemException;
import kr.co.mathrank.domain.problem.single.exception.CannotRegisterWithThisRoleException;

@SpringBootTest
class SingleProblemServiceTest {
	@Autowired
	private SingleProblemService singleProblemService;

	@Test
	void 어드민만_문제를_개별문제로_등록할_수_있다() {
		Assertions.assertDoesNotThrow(() -> singleProblemService.register(new SingleProblemRegisterCommand(1L, 2L, Role.ADMIN)));
	}

	@Test
	void 일반사용자는_개별문제_등록을_할_수_없다() {
		Assertions.assertThrows(CannotRegisterWithThisRoleException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(1L, 2L, Role.USER)));
	}

	@Test
	@Transactional
	void 이미_등록된_문제는_다시_등록할_수_없다() {
		final Long problemId = 1L;

		singleProblemService.register(new SingleProblemRegisterCommand(problemId, 2L, Role.ADMIN));
		Assertions.assertThrows(AlreadyRegisteredProblemException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(problemId, 2L, Role.ADMIN)));
	}

	@Test
	void 형식_검증() {

		Assertions.assertAll(
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> singleProblemService.register(null)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(null, 2L, Role.ADMIN))),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(1L, null, Role.ADMIN))),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> singleProblemService.register(new SingleProblemRegisterCommand(1L, 2L, null)))
		);
	}
}
