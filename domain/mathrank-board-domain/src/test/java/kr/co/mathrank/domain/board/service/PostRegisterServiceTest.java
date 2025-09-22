package kr.co.mathrank.domain.board.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.client.internal.member.MemberClient;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.board.dto.PostRegisterCommand;
import kr.co.mathrank.domain.board.entity.PostType;

@SpringBootTest
class PostRegisterServiceTest {
	@Autowired
	private PostRegisterService postRegisterService;
	@MockitoBean
	private PostMemberManager postMemberManager;

	@Test
	void 타입에_상관없이_디폴트_예외처리() {
		Assertions.assertAll(
			// 본문이 empty, 타입 지정
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postRegisterService.register(
				new PostRegisterCommand(PostType.CONTEST, "title", "", 1L, Role.ADMIN, 1L, 1L, 1L))),
			// 본문이 empty, 타입 지정 안됨
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postRegisterService.register(
				new PostRegisterCommand(null, "title", "", 1L, Role.ADMIN, 1L, 1L, 1L)))
		);
	}

	@Test
	void 게시글_타입이_지정안되면_예외() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> postRegisterService.register(
			new PostRegisterCommand(null, "title", "content", 1L, Role.ADMIN, 1L, 1L, 1L)));
	}

	@Test
	void 개별문제_게시글_동적_검증_테스트() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> postRegisterService.register(
			new PostRegisterCommand(PostType.SINGLE_PROBLEM, "title", "content", 1L, Role.ADMIN, null, 1L, 1L)));
	}

	@Test
	void 문제집_게시글_동적_검증_테스트() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> postRegisterService.register(
			new PostRegisterCommand(PostType.ASSESSMENT, "title", "content", 1L, Role.ADMIN, 1L, null, 1L)));
	}

	@Test
	void 대회_게시글_동적_검증_테스트() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> postRegisterService.register(
			new PostRegisterCommand(PostType.CONTEST, "title", "content", 1L, Role.ADMIN, 1L, 1L, null)));
	}

	@Test
	void 공지사항_게시글은_관리자만_작성가능하다() {
		Assertions.assertAll(
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postRegisterService.register(
				new PostRegisterCommand(PostType.NOTICE, "title", "content", 1L, Role.USER, 1L, 1L, null))),
			() -> Assertions.assertDoesNotThrow(() -> postRegisterService.register(
				new PostRegisterCommand(PostType.NOTICE, "title", "content", 1L, Role.ADMIN, 1L, 1L, null)))
		);
	}
}
