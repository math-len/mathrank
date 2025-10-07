package kr.co.mathrank.app.consumer.problem;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.mathrank.common.event.Event;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.event.publisher.monolith.MonolithEvent;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemDeleteCommand;
import kr.co.mathrank.domain.problem.single.service.SingleProblemDeleteService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemMonolithEventConsumer {
	private final SingleProblemDeleteService singleProblemDeleteService;
	private static final String PROBLEM_DELETED_EVENT_TOPIC = "problem-deleted";

	@Async
	@EventListener(MonolithEvent.class)
	public void consumeProblemDeletedEvent(final MonolithEvent monolithEvent) {
		if (!monolithEvent.isExpectedTopic(PROBLEM_DELETED_EVENT_TOPIC)) {
			return;
		}
		final Event<ProblemDeletedEvent> event = Event.fromJson(monolithEvent.payload(), ProblemDeletedEvent.class);
		singleProblemDeleteService.delete(new SingleProblemDeleteCommand(event.getPayload().id()));
	}

	record ProblemDeletedEvent(
		Long id,
		Long memberId,
		String problemImage,
		String solutionImage,
		Difficulty difficulty,
		AnswerType type,
		PastProblem pastProblem,
		String coursePath,
		String schoolCode,
		String location,
		Integer years,
		String solutionVideoLink
	) implements EventPayload {
	}
}
