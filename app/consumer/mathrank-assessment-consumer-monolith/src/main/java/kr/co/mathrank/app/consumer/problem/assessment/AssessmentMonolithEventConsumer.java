package kr.co.mathrank.app.consumer.problem.assessment;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.event.publisher.monolith.MonolithEvent;
import kr.co.mathrank.domain.problem.assessment.service.SubmissionGradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssessmentMonolithEventConsumer {
	private final SubmissionGradeService submissionGradeService;

	private static final String ASSESSMENT_REGISTERED_TOPIC = "assessment-submission-registered";

	@EventListener(MonolithEvent.class)
	@Async("assessmentMonolithEventConsumerExecutor")
	public void consume(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic(ASSESSMENT_REGISTERED_TOPIC)) {
			return;
		}
		log.debug("[AssessmentMonolithEventConsumer.consume] Monolith event received: {}", monolithEvent);

		try {
			final Event<SubmissionRegisteredEventPayload> event = Event.fromJson(
				monolithEvent.payload(),
				SubmissionRegisteredEventPayload.class
			);
			submissionGradeService.evaluateSubmission(event.getPayload().submissionId());
		} catch (Exception e) {
			log.error("[AssessmentMonolithEventConsumer.consume] Failed to process event. payload: {}", monolithEvent.payload(), e);
		}
	}

	record SubmissionRegisteredEventPayload(
		Long assessmentId,
		Long memberId,
		Long submissionId,
		LocalDateTime submittedTime,
		Duration elapsedTime
	) implements EventPayload {
	}
}
