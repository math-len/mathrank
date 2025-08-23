package kr.co.mathrank.domain.problem.single.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class SingleProblemRegisterManager {
	private final SingleProblemRepository singleProblemRepository;
	private final TransactionalOutboxPublisher outboxPublisher;

	@Transactional
	public void register(@NotNull final SingleProblem problem, @NotNull final ProblemQueryResult result) {
		final Long singleProblemId = singleProblemRepository.save(problem).getId();

		// 메시지 발행
		outboxPublisher.publish("single-problem-registered", new SingleProblemRegisteredEventPayload(
			singleProblemId,
			result.id(),
			problem.getSingleProblemName(),
			result.path(),
			result.imageSource(),
			result.type(),
			result.difficulty(),
			problem.getSingleProblemRegisteredAt(),
			problem.getFirstTrySuccessCount(),
			problem.getTotalAttemptedCount(),
			problem.getAttemptedUserDistinctCount()
		));
	}

	private record SingleProblemRegisteredEventPayload(
		Long singleProblemId,
		Long problemId,
		String singleProblemName,
		String coursePath,
		String problemImage,
		AnswerType answerType,
		Difficulty difficulty,
		LocalDateTime registeredAt,
		Long firstTrySuccessCount,
		Long totalAttemptedCount, // 문제를 풀려고 시도한 총 횟수
		Long attemptedUserDistinctCount // 해당 문제를 풀려고 한 사용자 수
	) implements EventPayload {
	}
}
