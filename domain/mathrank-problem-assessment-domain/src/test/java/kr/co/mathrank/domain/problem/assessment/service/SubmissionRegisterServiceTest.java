package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import kr.co.mathrank.domain.problem.core.Difficulty;

@SpringBootTest(properties = """
	spring.jpa.show-sql=true
	spring.jpa.properties.hibernate.format_sql=true
	spring.jpa.hibernate.ddl-auto=create
	""")
@Testcontainers
class SubmissionRegisterServiceTest {
	@Autowired
	private SubmissionRegisterService submissionRegisterService;
	@Autowired
	private AssessmentRegisterService assessmentRegisterService;
	@MockitoBean
	private ProblemClient problemClient;
	@Autowired
	private AssessmentRepository assessmentRepository;

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.42")
		.withDatabaseName("testdb")
		.withUsername("user")
		.withPassword("password");
	@Autowired
	private AssessmentSubmissionRepository assessmentSubmissionRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@Test
	void 같은_사용자가_동시에_여러번_제출할때_모든_답안지_정상등록() throws InterruptedException {
		final Long assessmentId = assessmentRegisterService.register(new AssessmentRegisterCommand(
			1L, Role.ADMIN,
			"testName",
			List.of(new AssessmentItemRegisterCommand(1L, 100)),
			Difficulty.KILLER,
			Duration.ofMinutes(100L)
		));

		final int tryCount = 20;

		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(tryCount);
		final Long memberId = 10L;

		for (int i = 0; i < tryCount; i++) {
			executorService.submit(() -> {
				try {
					submissionRegisterService.submit(
						new SubmissionRegisterCommand(memberId, assessmentId, List.of(List.of("test")),
							Duration.ofMinutes(1L)));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// 제출된 답안지는 trCount와 같아야한다
		Assertions.assertEquals(tryCount, assessmentSubmissionRepository.count());
	}

	@Test
	void 서로다른_사용자가_동시에_여러번_제출할때_모든_답안지_정상등록() throws InterruptedException {
		final Long assessmentId = assessmentRegisterService.register(new AssessmentRegisterCommand(
			1L, Role.ADMIN,
			"testName",
			List.of(new AssessmentItemRegisterCommand(1L, 100)),
			Difficulty.KILLER,
			Duration.ofMinutes(100L)
		));

		final int tryCount = 20;

		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(tryCount);

		for (int i = 0; i < tryCount; i++) {
			final Long memberId = (long) i;
			executorService.submit(() -> {
				try {
					submissionRegisterService.submit(
						new SubmissionRegisterCommand(memberId, assessmentId, List.of(List.of("test")),
							Duration.ofMinutes(1L)));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// 제출된 답안지 수는 tryCount와 같아야한다
		Assertions.assertEquals(tryCount, assessmentSubmissionRepository.count());
	}

	@Test
	void 여러_사용자가_동시에_답안지_제출시_정확하게_카운트() throws InterruptedException {
		final Long assessmentId = assessmentRegisterService.register(new AssessmentRegisterCommand(
			1L, Role.ADMIN,
			"testName",
			List.of(new AssessmentItemRegisterCommand(1L, 100)),
			Difficulty.KILLER,
			Duration.ofMinutes(100L)
		));

		final int tryCount = 10;

		final ExecutorService executorService = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(tryCount);

		for (int i = 0; i < tryCount; i++) {
			final Long memberId = (long) i;
			executorService.submit(() -> {
				try {
					submissionRegisterService.submit(
						new SubmissionRegisterCommand(memberId, assessmentId, List.of(List.of("test")),
							Duration.ofMinutes(1L)));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Assertions.assertEquals(tryCount, assessmentSubmissionRepository.count());
	}

	@Test
	void 동시제출될때_하나만_첫등록으로_기록된다() throws InterruptedException {
		final Long assessmentId = assessmentRegisterService.register(new AssessmentRegisterCommand(
			1L, Role.ADMIN,
			"testName",
			List.of(new AssessmentItemRegisterCommand(1L, 100)),
			Difficulty.KILLER,
			Duration.ofMinutes(100L)
		));

		final int tryCount = 10;

		final ExecutorService executorService = Executors.newFixedThreadPool(2);
		final CountDownLatch latch = new CountDownLatch(tryCount);
		final Long memberId = 10L; // 동일한 사용자 ID로 요청

		for (int i = 0; i < tryCount; i++) {
			executorService.submit(() -> {
				try {
					submissionRegisterService.submit(
						new SubmissionRegisterCommand(memberId, assessmentId, List.of(List.of("test")),
							Duration.ofMinutes(1L)));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Assertions.assertEquals(1, assessmentSubmissionRepository.findAll().stream()
			.map(AssessmentSubmission::getIsFirstSubmission)
			.filter(a -> a)
			.count());
	}

	@AfterEach
	void clear() {
		assessmentRepository.deleteAll();
	}
}
