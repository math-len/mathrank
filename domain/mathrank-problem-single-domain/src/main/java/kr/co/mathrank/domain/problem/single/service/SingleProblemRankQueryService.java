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

		return new SingleProblemRankResult(
			result.distinctUserCount(),
			challengeLog.getSuccess() ? getMyRank(result, myElapsedTime) : null, // 실패했을땐 등수 계산 X
			myElapsedTime,
			result.elapsedTimes(),
			result.averageElapsedTime()
		);
	}

	public SingleProblemRankResult queryRank(@NotNull @Valid final SingleProblemRankQuery query) {
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
			result.distinctUserCount(),
			firstChallengeLog.getSuccess() ? getMyRank(result, myElapsedTime) : null, // 실패했을땐 등수 계산 X
			myElapsedTime,
			result.elapsedTimes(),
			result.averageElapsedTime()
		);
	}

	private Integer getMyRank(final SingleProblemStatisticsResult result, final Duration myElapsedTime) {
		if (myElapsedTime == null) { // 풀이에 실패한 사용자일 경우, 항상 꼴등
			return null;
		}
		for (int i = 0; i < result.elapsedTimes().size(); i++) {
			if (result.elapsedTimes().get(i).compareTo(myElapsedTime) >= 0) { // 내 경과 시간 보다 큰 경우
				return i + 1;
			}
		}
		return result.elapsedTimes().size();
	}
}
