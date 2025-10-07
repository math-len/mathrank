package kr.co.mathrank.app.consumer.problem.assessment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.event.publisher.monolith.MonolithEvent;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDeleteCommand;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentDeleteService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentDifficultyService;
import kr.co.mathrank.domain.problem.assessment.service.SubmissionGradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssessmentMonolithEventConsumer {
	private final SubmissionGradeService submissionGradeService;
	private final AssessmentDifficultyService assessmentDifficultyService;

	private static final String ASSESSMENT_SUBMISSION_REGISTERED_TOPIC = "assessment-submission-registered";
	private static final String ASSESSMENT_REGISTERED_TOPIC = "assessment-registered";
	private static final String PROBLEM_DELETED_TOPIC = "problem-deleted";
	private final AssessmentDeleteService assessmentDeleteService;

	@EventListener(MonolithEvent.class)
	@Async("assessmentMonolithEventConsumerExecutor")
	public void consume(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic(ASSESSMENT_SUBMISSION_REGISTERED_TOPIC)) {
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

	@EventListener(MonolithEvent.class)
	@Async("assessmentMonolithEventConsumerExecutor")
	public void consumeAssessmentRegisteredEvent(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic(ASSESSMENT_REGISTERED_TOPIC)) {
			return;
		}
		log.debug("[AssessmentMonolithEventConsumer.consumeAssessmentRegisteredEvent] Monolith event received: {}", monolithEvent);

		try {
			final Event<AssessmentRegisteredEvent> event = Event.fromJson(
				monolithEvent.payload(),
				AssessmentRegisteredEvent.class
			);
			assessmentDifficultyService.updateToAverageDifficulty(event.getPayload().assessmentId);
		} catch (Exception e) {
			log.error("[AssessmentMonolithEventConsumer.consumeAssessmentRegisteredEvent] Failed to process event. payload: {}", monolithEvent.payload(), e);
		}
	}

	@EventListener(MonolithEvent.class)
	@Async
	public void consumeProblemDeletedEvent(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic(PROBLEM_DELETED_TOPIC)) {
			return;
		}
		log.debug("[AssessmentMonolithEventConsumer.consumeProblemDeletedEvent] Monolith event received: {}", monolithEvent);

		try {
			final Event<ProblemDeletedEvent> event = Event.fromJson(
				monolithEvent.payload(),
				ProblemDeletedEvent.class
			);
			assessmentDeleteService.deleteByProblemId(event.getPayload().id());
		} catch (Exception e) {
			log.error("[AssessmentMonolithEventConsumer.consumeProblemDeletedEvent] Failed to process event. payload: {}", monolithEvent.payload(), e);
		}
	}

	record SubmissionRegisteredEventPayload(
		Long assessmentId,
		Long memberId,
		Long submissionId,
		LocalDateTime submittedTime,
		Long elapsedTimeSeconds
	) implements EventPayload {
	}

	record AssessmentRegisteredEvent(
		Long assessmentId,
		Long registeredMemberId,
		String assessmentName,
		Long assessmentMinutes,
		List<Long> assessmentItemProblemIds,
		LocalDateTime createdAt
	) implements EventPayload {
	}

	record ProblemDeletedEvent(
		Long id,
		Long memberId,
		String problemImage,
		String solutionImage,
		String coursePath,
		String schoolCode,
		String location,
		Integer years,
		String solutionVideoLink
	) implements EventPayload {
	}
}
