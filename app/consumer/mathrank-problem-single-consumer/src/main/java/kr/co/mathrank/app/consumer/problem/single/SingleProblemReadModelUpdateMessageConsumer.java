package kr.co.mathrank.app.consumer.problem.single;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemAttemptStatsUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.service.SingleProblemUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 개별 문제 조회용 Read Model을 업데이트하는 Kafka 메시지 컨슈머입니다.
 * <p>
 * 이 컨슈머는 문제 정보(예: 내용, 난이도) 변경 이벤트와 문제 풀이 통계 변경 이벤트를 수신하여,
 * CQRS 패턴의 조회(Query) 측 모델을 최신 상태로 유지하는 역할을 담당합니다.
 *
 * 메시지 포맷은 <a href="https://snow-quasar-645.notion.site/Message-Format-249631417ede80a6a7dade6a34c0420a?pvs=73">
 * 이 문서</a>에 설명되어 있습니다.
 *
 * @see SingleProblemUpdateService
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SingleProblemReadModelUpdateMessageConsumer {
	private final SingleProblemUpdateService singleProblemUpdateService;

	static final String GROUP_ID = "single-problem-read-model-updaters";

	// 문제 정보(내용, 이미지, 난이도 등) 변경 이벤트 토픽
	static final String PROBLEM_INFO_UPDATED_TOPIC = "problem-info-updated";
	// 문제 풀이 통계(제출 수, 정답 수) 변경 이벤트 토픽
	static final String SINGLE_PROBLEM_STATISTICS_UPDATED_TOPIC = "single-problem-statistics-updated";

	/**
	 * 문제 정보 변경 이벤트를 수신하여 Read Model을 업데이트합니다.
	 *
	 * @param message Kafka로부터 수신한 JSON 형식의 이벤트 메시지
	 */
	@KafkaListener(
		groupId = GROUP_ID,
		topics = PROBLEM_INFO_UPDATED_TOPIC
	)
	public void consumeProblemInfoUpdatedMessage(final String message) {
		final Event<ProblemUpdatedPayload> event = Event.fromJson(message, ProblemUpdatedPayload.class);
		final SingleProblemReadModelUpdateCommand command = event.getPayload().toCommand();

		singleProblemUpdateService.updateProblemInfo(command);
	}

	/**
	 * 문제 풀이 통계 변경 이벤트를 수신하여 Read Model을 업데이트합니다.
	 *
	 * @param message Kafka로부터 수신한 JSON 형식의 이벤트 메시지
	 */
	@KafkaListener(
		groupId = GROUP_ID,
		topics = SINGLE_PROBLEM_STATISTICS_UPDATED_TOPIC
	)
	public void consumeSingleProblemStatisticsUpdatedMessage(final String message) {
		final Event<SingleProblemStatisticsUpdatedPayload> event = Event.fromJson(message,
			SingleProblemStatisticsUpdatedPayload.class);
		final SingleProblemAttemptStatsUpdateCommand command = event.getPayload().toCommand();

		singleProblemUpdateService.updateAttemptStatistics(command);
	}
}
