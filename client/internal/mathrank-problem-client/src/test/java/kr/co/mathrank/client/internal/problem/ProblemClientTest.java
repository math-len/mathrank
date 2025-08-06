package kr.co.mathrank.client.internal.problem;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;


@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = """
		spring.jpa.show-sql=true
		""")
class ProblemClientTest {
	@LocalServerPort
	private int port;

	@Test
	void api연결이_정상적인지_확인한다() {
		final ProblemClient.ProblemClientProperties props = new ProblemClient.ProblemClientProperties();
		props.setHost("http://localhost");
		props.setPort(port);
		final ProblemClient problemClient = new ProblemClient(props);

		Assertions.assertThrows(HttpClientErrorException.class, () -> problemClient.matchAnswer(1L, Collections.emptyList()));
	}
}