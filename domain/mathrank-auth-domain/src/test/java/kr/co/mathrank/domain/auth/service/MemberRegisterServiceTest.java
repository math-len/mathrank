package kr.co.mathrank.domain.auth.service;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.MemberRegisterCommand;
import kr.co.mathrank.domain.auth.entity.MemberType;
import kr.co.mathrank.domain.auth.entity.Password;
import kr.co.mathrank.domain.auth.exception.AuthException;

@SpringBootTest
@Transactional
class MemberRegisterServiceTest {
	@Autowired
	private MemberRegisterService memberRegisterService;

	@Test
	void 아이디가_이미_존재할때_에러처리() {
		final String duplicatedId = "loginId";
		final String userName = "testName";
		final Password password = new Password("1234");

		final MemberRegisterCommand command = new MemberRegisterCommand(duplicatedId, userName, password, Role.USER, MemberType.NORMAL, true,
			null);

		memberRegisterService.register(command);
		Assertions.assertThrows(AuthException.class, () -> memberRegisterService.register(command));
	}

	@Test
	void 형식_에러_테스트() {
		Assertions.assertAll(
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> memberRegisterService.register(null)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> memberRegisterService.register(new MemberRegisterCommand(null, null, null, null, null, null, null)))
		);
	}
}
