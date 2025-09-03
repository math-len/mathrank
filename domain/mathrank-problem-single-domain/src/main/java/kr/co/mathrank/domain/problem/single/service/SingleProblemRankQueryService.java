package kr.co.mathrank.domain.problem.single.service;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRankQuery;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRankResult;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemStatisticsResult;
import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;
import kr.co.mathrank.domain.problem.single.entity.Challenger;
import kr.co.mathrank.domain.problem.single.exception.CannotFindChallengeLogException;
import kr.co.mathrank.domain.problem.single.exception.CannotFindChallengerException;
import kr.co.mathrank.domain.problem.single.repository.ChallengeLogRepository;
import kr.co.mathrank.domain.problem.single.repository.ChallengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemRankQueryService {
	private final SingleProblemStatisticsService singleProblemStatisticsService;
	private final ChallengerRepository challengerRepository;
	private final ChallengeLogRepository challengeLogRepository;

	public SingleProblemRankResult queryRank(@NotNull final Long challengeLogId) {
		final ChallengeLog challengeLog = challengeLogRepository.findWithSingleProblem(challengeLogId)
			.orElseThrow(() -> {
				log.info("[SingleProblemRankQueryService.queryChallengeLogRank] cannot find challenge log - challengeLogId: {}", challengeLogId);
				return new CannotFindChallengeLogException();
			});
		final SingleProblemStatisticsResult result = singleProblemStatisticsService.loadFirstTrySucceedStatistics(challengeLog.getChallenger().getSingleProblem().getId());
		final Duration myElapsedTime = challengeLog.getElapsedTime();

		// 조회하는 기록이 첫번째 풀이임
		if (challengeLog.isFirstTry()) {
			return new SingleProblemRankResult(
				challengeLog.getSuccess() ? findRankIndex(result, myElapsedTime, result.elapsedTimes().size()) : null, // 실패했을땐 등수 계산 X
				result.elapsedTimes().size(),
				myElapsedTime,
				result.elapsedTimes(),
				result.averageElapsedTime()
			);
		}

		// 조회하는 기록이 첫번째 풀이가 아님
		return new SingleProblemRankResult(
			challengeLog.getSuccess() ? findRankIndex(result, myElapsedTime, result.elapsedTimes().size() + 1) : null, // 실패했을땐 등수 계산 X
			result.elapsedTimes().size() + 1,
			myElapsedTime,
			result.elapsedTimes(),
			result.averageElapsedTime()
		);
	}

	public SingleProblemRankResult queryFirstTriedLogRank(@NotNull @Valid final SingleProblemRankQuery query) {
		final SingleProblemStatisticsResult result = singleProblemStatisticsService.loadFirstTrySucceedStatistics(query.singleProblemId());

		final Challenger challenger = challengerRepository.findByMemberIdAndSingleProblemId(
				query.requestMemberId(), query.singleProblemId())
			.orElseThrow(() -> {
				log.info(
					"[SingleProblemRankQueryService.queryRank] cannot find challenger - singleProblemId: {}, requestMemberId: {}",
					query.singleProblemId(), query.requestMemberId());
				return new CannotFindChallengerException();
			});
		final ChallengeLog firstChallengeLog = challenger.getChallengeLogs().getFirst();

		final Duration myElapsedTime = firstChallengeLog.getElapsedTime();

		return new SingleProblemRankResult(
			firstChallengeLog.getSuccess() ? findRankIndex(result, myElapsedTime, result.elapsedTimes().size()) : null, // 실패했을땐 등수 계산 X
			result.elapsedTimes().size(),
			myElapsedTime,
			result.elapsedTimes(),
			result.averageElapsedTime()
		);
	}

	/**
	 * @param result
	 * @param myElapsedTime
	 * @return 주어진 리스트에서 자신의 랭크를 반환한다.
	 * 주어진 리스트에서 자신보다 첫번째로 크거나 같은놈의 (인덱스 + 1)을 반환한다.
	 */
	private Integer findRankIndex(final SingleProblemStatisticsResult result, final Duration myElapsedTime, final Integer totalUserCount) {
		for (int i = 0; i < result.elapsedTimes().size(); i++) {
			if (result.elapsedTimes().get(i).compareTo(myElapsedTime) >= 0) { // 내 경과 시간 보다 큰 경우
				return i + 1;
			}
		}

		// 꼴등
		return totalUserCount;
	}
}
