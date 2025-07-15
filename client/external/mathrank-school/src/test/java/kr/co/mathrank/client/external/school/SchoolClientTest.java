package kr.co.mathrank.client.external.school;

import java.util.Optional;

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
		Assertions.assertFalse(schoolResponse.getSchoolInfo().isEmpty());
	}

	@Test
	void 결과가_없을때() {
		final SchoolResponse schoolResponse = schoolClient.getSchools(RequestType.JSON.getType(), 2, 10, "ㅂㅈ두버ㅕ룾ㅂ");
		Assertions.assertTrue(schoolResponse.getSchoolInfo().isEmpty());
	}

	@Test
	void 학교코드로_조회_성공_태스트() {
		final Optional<SchoolInfo> schoolInfo = schoolClient.getSchool(RequestType.JSON.getType(), "7150129");
		Assertions.assertTrue(schoolInfo.isPresent());
	}

	@Test
	void 학교코드_존재안할떄_빈_컬렉션_리턴() {
		final Optional<SchoolInfo> info = schoolClient.getSchool(RequestType.JSON.getType(), "7150129123ㅇㅁㅈㅇㄴㅇ");
		Assertions.assertTrue(info.isEmpty());
	}
}
