package kr.co.mathrank.domain.auth.util;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriUtils;

class UriUtilEncodeTest {
	@Test
	void plus_문자_인코딩_테스트() {
		Assertions.assertNotEquals("+", UriUtils.encode("+", Charset.defaultCharset()));
		Assertions.assertEquals("+", UriUtils.decode("+", Charset.defaultCharset()));
	}
}
