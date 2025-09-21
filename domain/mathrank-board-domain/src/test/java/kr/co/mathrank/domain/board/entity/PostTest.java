package kr.co.mathrank.domain.board.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
	properties = """
		spring.jpa.show-sql=true
		spring.jpa.properties.hibernate.format_sql=true
		"""
)
class PostTest {
	@Test
	void 상속시_일대일_매핑확인() {

	}
}