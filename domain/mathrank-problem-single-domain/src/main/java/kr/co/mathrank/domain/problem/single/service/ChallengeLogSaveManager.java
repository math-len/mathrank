package kr.co.mathrank.domain.problem.single.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.common.dataserializer.DataSerializer;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.exception.CannotFindSingleProblemException;
import kr.co.mathrank.domain.problem.single.repository.ChallengeLogRepository;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class ChallengeLogSaveManager {
	private final Snowflake snowflake;
	private final SingleProblemRepository singleProblemRepository;
	private final ChallengeLogRepository challengeLogRepository;

	@Transactional
	public void saveLog(final Long singleProblemId, final Long memberId, final SolveResult solveResult) {
		// 사용자 풀이 횟수 락걸기
		final SingleProblem singleProblem = singleProblemRepository.findByIdForUpdate(singleProblemId)
			.orElseThrow(CannotFindSingleProblemException::new);

		// s-lock으로 조회
		// single Problem의 challenge log가 수만개까지 쌓일 수 있음으로, DB 인덱스를 통해 조회하도록 한다.
		// memberId, singleProblemId 를 통한 쿼리 결과는 max 100개 정도로 예상됨
		final Boolean alreadyTried = !challengeLogRepository.findAllBySingleProblemIdAndChallengerMemberIdForShare(singleProblemId, memberId).isEmpty();

		// 해당 사용자의 풀이 기록이 없을떄 성공 카운트를 증가한다.
		if (alreadyTried) {
			singleProblem.increaseAttemptCount();
		} else {
			singleProblem.firstTry(solveResult.success());
		}

		final ChallengeLog challengeLog = ChallengeLog.of(
			snowflake.nextId(),
			singleProblem,
			memberId,
			solveResult.success(),
			DataSerializer.serialize(solveResult.submittedAnswer()).orElse("null"),
			DataSerializer.serialize(solveResult.realAnswer()).orElse("null"),
			LocalDateTime.now());
		challengeLogRepository.save(challengeLog);

		log.info("[SingleProblemService.solve] solve log registered - singleProblemId: {}, memberId: {}, success: {}",
			singleProblem.getId(), memberId, solveResult.success());
	}
}
