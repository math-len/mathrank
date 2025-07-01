package kr.co.mathrank.client.external.school;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SchoolClientTest {
	@Autowired
	private SchoolClient schoolClient;

	@Test
	void test() {
		final SchoolResponse schoolResponse = schoolClient.getSchools(null, RequestType.JSON.getType(), 2, 10, "대연");
		Assertions.assertNotNull(schoolResponse);
	}
}