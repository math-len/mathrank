package kr.co.mathrank.app.consumer.problem.single;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.service.SingleProblemUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleProblemReadModelUpdateMessageConsumer {
	private final SingleProblemUpdateService singleProblemUpdateService;

	static final String GROUP_ID = "single-problem-read-model-updaters";
	static final String PROBLEM_INFO_UPDATED_TOPIC = "problem-info-updated";

	@KafkaListener(
		groupId = GROUP_ID,
		topics = PROBLEM_INFO_UPDATED_TOPIC
	)
	public void consumeProblemInfoUpdatedMessage(final String message) {
		log.debug("[SingleProblemReadModelConsumer.consumeProblemInfoUpdatedMessage] received message: {}", message);
		final Event<ProblemUpdatedPayload> event = Event.fromJson(message, ProblemUpdatedPayload.class);
		final SingleProblemReadModelUpdateCommand command = event.getPayload().toCommand();

		singleProblemUpdateService.updateProblemInfo(command);
	}
}
