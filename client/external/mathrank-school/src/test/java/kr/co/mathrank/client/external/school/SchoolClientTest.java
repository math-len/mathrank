package kr.co.mathrank.client.external.school;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class SchoolClientTest {
	@Autowired
	private SchoolClient schoolClient;

	@Test
	void 결과가_있을때() {
		final SchoolResponse schoolResponse = schoolClient.getSchools(RequestType.JSON.getType(), 2, 10, "대연");
		System.out.println(schoolResponse);
		Assertions.assertFalse(schoolResponse.schoolInfo().isEmpty());
	}

	@Test
	void 결과가_없을때() {
		final SchoolResponse schoolResponse = schoolClient.getSchools(RequestType.JSON.getType(), 2, 10, "ㅂㅈ두버ㅕ룾ㅂ");
		Assertions.assertNull(schoolResponse.schoolInfo());
	}
}
