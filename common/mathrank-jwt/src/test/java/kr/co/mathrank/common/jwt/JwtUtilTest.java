package kr.co.mathrank.common.jwt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.common.role.Role;

@SpringBootTest(
	properties = """
		server.jwt.algorithm=HS256
		server.jwt.key=testqwekjnewqnjoeqwojdinewiofuqnrfourqnfoueruojdclkdsmncokdewfnoerjufnqourebfiuernjvfvpieqrnpovjeoqjrunbvruydhfedo23uf802euwjfnelnewfj
		"""
)
class JwtUtilTest {
	@Autowired
	private JwtUtil jwtUtil;

	@Test
	void 토큰을_파싱시_저장된_값이_리턴된다() {
		final long memberId = 1L;
		final Role memberRole = Role.USER;

		final JwtResult result = jwtUtil.createJwt(memberId, memberRole, 5_000L, 10_000L);
		final UserInfo info = jwtUtil.parse(result.accessToken());

		Assertions.assertAll(
			() -> Assertions.assertEquals(memberId, info.userId()),
			() -> Assertions.assertEquals(memberRole, info.role())
		);
	}

	@Test
	void 유효기간이_지나면_에러발생한다() {
		final long memberId = 1L;
		final Role memberRole = Role.USER;

		final JwtResult result = jwtUtil.createJwt(memberId, memberRole, 0L, 10_000L);

		Assertions.assertThrows(JwtException.class, () -> jwtUtil.parse(result.accessToken()));
	}

	@Test
	void 검증_로직_정상동작_확인() {
		Assertions.assertAll(
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> jwtUtil.createJwt(null, null, null, null)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> jwtUtil.parse(null))
		);
	}

	@Test
	void 매순간_다른_JWT를_생성한다() {
		final long memberId = 1L;
		final Role memberRole = Role.USER;

		final JwtResult result1 = jwtUtil.createJwt(memberId, memberRole, 0L, 10_000L);
		final JwtResult result2 = jwtUtil.createJwt(memberId, memberRole, 0L, 10_000L);

		Assertions.assertNotEquals(result1, result2);
	}
}