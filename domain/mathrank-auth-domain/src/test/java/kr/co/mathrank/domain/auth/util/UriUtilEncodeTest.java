package kr.co.mathrank.domain.auth.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriUtils;

class UriUtilEncodeTest {
	@Test
	void plus_문자_인코딩_테스트() {
		Assertions.assertNotEquals("+", UriUtils.encode("+", StandardCharsets.UTF_8));
		Assertions.assertEquals("+", UriUtils.decode("+", StandardCharsets.UTF_8));
	}
}
