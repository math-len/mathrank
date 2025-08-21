package kr.co.mathrank.app.consumer.problem.single;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemAttemptStatsUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.service.SingleProblemUpdateService;

@SpringBootTest(classes = {
	SingleProblemReadModelUpdateMessageConsumer.class,
	SingleProblemReadModelConsumerConfiguration.class,
	KafkaAutoConfiguration.class})
@TestPropertySource(
	properties = "spring.kafka.consumer.auto-offset-reset=earliest" // 리밸런싱 후, 토픽의 첫 오프셋 부터 읽도록 설정
)
@EmbeddedKafka
@DisplayName("SingleProblemReadModelConsumer 통합 테스트")
class SingleProblemReadModelConsumerTest {
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	// 실제 서비스 대신 Mock 객체를 주입하여 상호작용을 검증합니다.
	@MockitoBean
	private SingleProblemUpdateService singleProblemUpdateService;

	@Test
	void 유효한_업데이트_이벤트를_수신하면_페이로드를_파싱하여_서비스_로직을_호출한다() {
		// given
		// 테스트용 페이로드와 이벤트를 생성합니다.
		final var payload = new ProblemUpdatedPayload(
			101L,
			"수학I > 지수함수와 로그함수",
			"problem.png",
			AnswerType.MULTIPLE_CHOICE,
			Difficulty.LOW,
			LocalDateTime.now()
		);
		final Event<ProblemUpdatedPayload> event = Event.of(1L, payload);
		final String message = event.serialize();

		// when
		// 생성한 메시지를 Kafka 토픽으로 전송합니다.
		kafkaTemplate.send(SingleProblemReadModelUpdateMessageConsumer.PROBLEM_INFO_UPDATED_TOPIC, message);

		// then
		// 비동기 처리를 기다린 후, 서비스가 올바르게 호출되었는지 검증합니다.
		final ArgumentCaptor<SingleProblemReadModelUpdateCommand> commandCaptor =
			ArgumentCaptor.forClass(SingleProblemReadModelUpdateCommand.class);

		// Awaitility를 사용하여 최대 5초 동안 100ms 간격으로 singleProblemUpdateService.updateProblemInfo가 1번 호출될 때까지 기다립니다.
		await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
			.untilAsserted(() -> verify(singleProblemUpdateService, times(1)).updateProblemInfo(commandCaptor.capture())
			);

		// 서비스에 전달된 Command 객체의 내용이 페이로드와 일치하는지 확인합니다.
		final SingleProblemReadModelUpdateCommand capturedCommand = commandCaptor.getValue();
		assertThat(capturedCommand.problemId()).isEqualTo(payload.problemId());
		assertThat(capturedCommand.coursePath()).isEqualTo(payload.coursePath());
		assertThat(capturedCommand.problemImage()).isEqualTo(payload.problemImage());
		assertThat(capturedCommand.answerType()).isEqualTo(payload.answerType());
		assertThat(capturedCommand.difficulty()).isEqualTo(payload.difficulty());
	}

	@Test
	@DisplayName("유효하지 않은 형식의 메시지를 수신하면, 서비스 로직을 호출하지 않는다")
	void 유효하지_않은_형식의_메시지를_수신하면_서비스_로직을_호출하지_않는다() throws InterruptedException {
		// given
		final String invalidMessage = "{\"key\":\"this is not a valid event format\"}";

		// when
		kafkaTemplate.send(SingleProblemReadModelUpdateMessageConsumer.PROBLEM_INFO_UPDATED_TOPIC, invalidMessage);

		// then
		// 메시지 처리 및 에러 핸들링에 시간이 걸릴 수 있으므로 잠시 대기합니다.
		Thread.sleep(1000);

		// 서비스의 어떤 메소드도 호출되지 않았음을 검증합니다.
		verifyNoInteractions(singleProblemUpdateService);
	}

	@Test
	@DisplayName("유효한 통계 업데이트 이벤트를 수신하면, 서비스 로직을 호출한다")
	void whenGivenValidEvent_thenCallsService() {
		// given
		final long singleProblemId = 123L;
		final long firstTrySuccessCount = 1L;
		final long attemptedUserDistinctCount = 2L;
		final long totalAttemptedCount = 3L;
		final var payload = new SingleProblemStatisticsUpdatedPayload(singleProblemId, 2L, 3L, true, firstTrySuccessCount,
			totalAttemptedCount, attemptedUserDistinctCount);
		final Event<SingleProblemStatisticsUpdatedPayload> event = Event.of(1L, payload);
		final String message = event.serialize();

		// when
		kafkaTemplate.send(SingleProblemReadModelUpdateMessageConsumer.SINGLE_PROBLEM_STATISTICS_UPDATED_TOPIC,
			message);

		// then
		final ArgumentCaptor<SingleProblemAttemptStatsUpdateCommand> commandCaptor =
			ArgumentCaptor.forClass(SingleProblemAttemptStatsUpdateCommand.class);

		// Awaitility를 사용하여 최대 5초 동안 서비스가 호출될 때까지 기다립니다.
		await().atMost(5, TimeUnit.SECONDS)
			.untilAsserted(() ->
				verify(singleProblemUpdateService, times(1)).updateAttemptStatistics(commandCaptor.capture())
			);

		// 서비스에 전달된 Command 객체의 내용이 페이로드와 일치하는지 확인합니다.
		final SingleProblemAttemptStatsUpdateCommand capturedCommand = commandCaptor.getValue();
		assertThat(capturedCommand.singleProblemId()).isEqualTo(singleProblemId);
		assertThat(capturedCommand.firstTrySuccessCount()).isEqualTo(firstTrySuccessCount);
		assertThat(capturedCommand.attemptedUserDistinctCount()).isEqualTo(attemptedUserDistinctCount);
		assertThat(capturedCommand.totalAttemptedCount()).isEqualTo(totalAttemptedCount);
	}

	@Test
	@DisplayName("유효하지 않은 형식의 메시지를 수신하면, 서비스 로직을 호출하지 않는다")
	void whenGivenInvalidEvent_thenDoesNotCallService() {
		// given
		final String invalidMessage = "this is not a valid json";

		// when
		kafkaTemplate.send(SingleProblemReadModelUpdateMessageConsumer.SINGLE_PROBLEM_STATISTICS_UPDATED_TOPIC,
			invalidMessage);

		// then
		// Mockito의 after()를 사용하여 1초 동안 기다린 후에도 서비스가 호출되지 않았음을 검증합니다.
		verify(singleProblemUpdateService, after(1000).never()).updateAttemptStatistics(any());
	}
}
