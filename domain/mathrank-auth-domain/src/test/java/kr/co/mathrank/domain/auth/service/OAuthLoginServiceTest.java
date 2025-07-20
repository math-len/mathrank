package kr.co.mathrank.domain.auth.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import kr.co.mathrank.domain.auth.client.MemberInfo;
import kr.co.mathrank.domain.auth.client.OAuthClientManager;
import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;
import kr.co.mathrank.domain.auth.repository.MemberRepository;

@SpringBootTest
class OAuthLoginServiceTest {
	@Autowired
	private OAuthLoginService oAuthLoginService;
	@MockitoBean
	private OAuthClientManager oAuthClientManager;
	@Autowired
	private MemberRepository memberRepository;
	@MockitoBean
	private JwtLoginManager jwtLoginManager;

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.42")
		.withDatabaseName("testdb")
		.withUsername("user")
		.withPassword("password");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		mysql.start();
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Test
	void 같은_oAuth계정으로_중복_회원가입을_방지한다() {
		final int tryCount = 100;
		final int userCount = 20;
		final ExecutorService executorService = Executors.newFixedThreadPool(userCount);
		final OAuthProvider kakao = OAuthProvider.KAKAO;
		final OAuthLoginCommand command = new OAuthLoginCommand("testCode", "testState", kakao);

		final long oAuthId = 12345L;
		final MemberInfo memberInfo = new MemberInfo(oAuthId, "nickName");
		// 항상 memberInfo 리턴
		Mockito.when(oAuthClientManager.getMemberInfo(command)).thenReturn(memberInfo);

		final CountDownLatch countDownLatch = new CountDownLatch(tryCount);

		for (int i = 0; i < tryCount; i++) {
			executorService.submit(() -> {
				try {
					oAuthLoginService.login(command);
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Assertions.assertEquals(1, memberRepository.findAllByOAuthIdAndProviderNoLock(oAuthId, kakao).size());
	}
}
