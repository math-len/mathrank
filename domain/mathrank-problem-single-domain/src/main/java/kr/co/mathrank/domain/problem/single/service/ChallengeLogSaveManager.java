package kr.co.mathrank.domain.problem.single.service;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveResult;
import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;
import kr.co.mathrank.domain.problem.single.entity.Challenger;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.exception.CannotFindSingleProblemException;
import kr.co.mathrank.domain.problem.single.repository.ChallengeLogRepository;
import kr.co.mathrank.domain.problem.single.repository.ChallengerRepository;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class ChallengeLogSaveManager {
	private final SingleProblemRepository singleProblemRepository;
	private final ChallengerRepository challengerRepository;
	private final TransactionalOutboxPublisher outboxPublisher;
	private final ChallengeLogRepository challengeLogRepository;

	@Transactional
	public Long saveLog(
		@NotNull final Long singleProblemId,
		@NotNull final Long memberId,
		@NotNull @Valid final SingleProblemSolveResult solveResult,
		@NotNull final Duration elapsedTime
	) {
		// 락걸기
		final SingleProblem singleProblem = singleProblemRepository.findByIdForUpdate(singleProblemId)
			.orElseThrow(CannotFindSingleProblemException::new);
		final ChallengeLog savedLog =
			challengerRepository.findByMemberIdAndSingleProblemIdForShare(memberId, singleProblemId)
				.map(challenger -> {
					// 이미 해당 사용자가 푼 적 있음
					singleProblem.increaseAttemptCount();
					final ChallengeLog log = addChallengeLog(solveResult, challenger, singleProblem, elapsedTime);
					publishChallengeLog(log, singleProblem, challenger, elapsedTime);
					return log;
				})
				.orElseGet(() -> {
					// 사용자가 처음 품
					singleProblem.firstTry(solveResult.success(), elapsedTime);
					final Challenger challenger = Challenger.of(memberId, singleProblem);
					final ChallengeLog log = addChallengeLog(solveResult, challenger, singleProblem, elapsedTime);
					challengerRepository.save(challenger);
					publishChallengeLog(log, singleProblem, challenger, elapsedTime);
					return log;
				});

		log.info("[SingleProblemService.solve] solve log registered - singleProblemId: {}, memberId: {}, success: {}",
			singleProblem.getId(), memberId, solveResult.success());
		challengeLogRepository.save(savedLog);

		return savedLog.getId();
	}

	private ChallengeLog addChallengeLog(SingleProblemSolveResult solveResult, Challenger challenger, SingleProblem singleProblem, Duration elapsedTime) {
		return challenger.addChallengeLog(solveResult.success(), solveResult.submittedAnswer(), solveResult.realAnswer().stream()
			.toList(), elapsedTime);
	}

	private void publishChallengeLog(final ChallengeLog challengeLog, final SingleProblem singleProblem, final Challenger challenger, final Duration elapsedTime) {
		outboxPublisher.publish("single-problem-solved", new SingleProblemSolvedEventPayload(
			singleProblem.getId(),
			singleProblem.getProblemId(),
			challenger.getMemberId(),
			challengeLog.getSuccess(),
			singleProblem.getFirstTrySuccessCount(),
			singleProblem.getTotalAttemptedCount(),
			singleProblem.getAttemptedUserDistinctCount(),
			elapsedTime.getSeconds()
		));
	}

	private record SingleProblemSolvedEventPayload(
		Long singleProblemId,
		Long problemId,
		Long memberId,
		Boolean success,
		Long firstTrySuccessCount,
		Long totalAttemptedCount,
		Long attemptedUserDistinctCount,
		Long elapsedSeconds
	) implements EventPayload {
	}
}
