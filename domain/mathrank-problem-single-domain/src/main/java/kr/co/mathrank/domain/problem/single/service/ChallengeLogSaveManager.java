package kr.co.mathrank.domain.problem.single.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.client.internal.problem.SolveResult;
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

		/*
			개념:  mysql에서 MVCC로 트랜잭션 내 첫 non-locking-read 실행 시점에 스냅샷 생성.

			로직:
			 1) 각 트랜잭션은 락 획득 전까지 스냅샷을 생성하지 않는다.
			 2) 락 획득 후, 아래 쿼리(non-locking-read)를 통해 스냅샷을 생성한다.
			 3) 스냅샷은 최신 커밋 데이터를 기준으로 스냅샷이 생성됨으로, 동시성 문제 없이 데이터를 조회한다.

			 https://dev.mysql.com/doc/refman/8.4/en/innodb-consistent-read.html
		*/
		// 이는, 후에 challengLog가 많아졌을 시, 전체 메모리 로드를 방지하기 위함
		final Boolean alreadyTried = singleProblemRepository.getAlreadySolved(singleProblemId, memberId);

		// 해당 사용자의 풀이 기록이 없을떄 성공 카운트를 증가한다.
		if (alreadyTried) {
			singleProblem.increaseAttemptCount();
		} else {
			singleProblem.firstTry(solveResult.success());
		}

		final ChallengeLog challengeLog = ChallengeLog.of(snowflake.nextId(), singleProblem, memberId,
			solveResult.success(), solveResult.success().toString(), solveResult.realAnswer().toString(),
			LocalDateTime.now());
		challengeLogRepository.save(challengeLog);

		log.info("[SingleProblemService.solve] solve log registered - singleProblemId: {}, memberId: {}, success: {}",
			singleProblem.getId(), memberId, solveResult.success());
	}
}
