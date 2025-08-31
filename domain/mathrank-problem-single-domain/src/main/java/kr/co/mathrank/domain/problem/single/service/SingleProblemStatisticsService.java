package kr.co.mathrank.domain.problem.single.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemStatisticsResult;
import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;
import kr.co.mathrank.domain.problem.single.entity.Challenger;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;

/**
 * 개별 문제의 통계를 가져오는 API
 */
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemStatisticsService {
	private final SingleProblemRepository singleProblemRepository;

	@Transactional(readOnly = true)
	public SingleProblemStatisticsResult loadFirstTrySucceedStatistics(@NotNull final Long singleProblemId) {
		final SingleProblem singleProblem = singleProblemRepository.findWithChallengers(singleProblemId)
			.orElseThrow();

		final List<Challenger> succeedChallengers = singleProblem.getSucceededChallengers();
		final List<Duration> succeededElapsedTimes = mapToElapsedTimesgetElapsedTimes(succeedChallengers);
		final Duration averageElapsedTime = getAverage(succeededElapsedTimes);

		return new SingleProblemStatisticsResult(
			singleProblemId,
			succeededElapsedTimes,
			averageElapsedTime,
			succeedChallengers.size(),
			singleProblem.getAttemptedUserDistinctCount()
		);
	}

	private List<Duration> mapToElapsedTimesgetElapsedTimes(final List<Challenger> succeedChallengers) {
		return succeedChallengers.stream()
			.map(challenger -> challenger.getChallengeLogs().getFirst())
			.map(ChallengeLog::getElapsedTime)
			.toList();
	}

	private Duration getAverage(final List<Duration> getFirstTrySucceededElapsedTimes) {
		return getFirstTrySucceededElapsedTimes.stream()
			.reduce(Duration.ZERO, Duration::plus)
			.dividedBy(getFirstTrySucceededElapsedTimes.isEmpty() ? 1 : getFirstTrySucceededElapsedTimes.size());
	}
}
