package kr.co.mathrank.client.internal.member;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = """
		spring.jpa.show-sql=true
client.member.connection-timeout-seconds=10
client.member.read-timeout-seconds=10
		""")
@Transactional
class MemberClientTest {
	@LocalServerPort
	private Integer port;

	@Test
	void api_연동_테스트() throws InterruptedException {
		// 트랜잭션 경계문제로 실제 데이터 삽입 테스트는 여기서 수행하지 않는다.
		// api 연동이 되는지만 확인한다.
		final MemberClient.MemberClientProperties props = new MemberClient.MemberClientProperties();
		props.setPort(port);
		props.setConnectionTimeoutSeconds(1);
		props.setReadTimeoutSeconds(1);
		final MemberClient memberClient = new MemberClient(props);

		Assertions.assertEquals(new MemberInfo(null, null), memberClient.getMemberInfo(1L));
	}
}
