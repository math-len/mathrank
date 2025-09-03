package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionRankResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionStatisticQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchSubmissionException;
import kr.co.mathrank.domain.problem.assessment.exception.SubmissionNotEvaluatedException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AssessmentRankQueryService {
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;
	private final AssessmentStatisticsService assessmentStatisticsService;

	public AssessmentSubmissionRankResult getRank(final Long submissionId) {
		final AssessmentSubmission assessmentSubmission = assessmentSubmissionRepository.findById(submissionId)
			.orElseThrow(() -> {
				log.info("[AssessmentRankQueryService.getRank] cannot found assessment submission - submissionId: {}", submissionId);
				return new NoSuchSubmissionException();
			});

		// 채점 안됐으면 불가
		if (assessmentSubmission.getEvaluationStatus() != EvaluationStatus.FINISHED) {
			log.info("[AssessmentRankQueryService.getRank] submission not evaluated - submissionId: {}", submissionId);
			throw new SubmissionNotEvaluatedException();
		}

		// 해당 문제집 풀이 기록 조회
		final AssessmentSubmissionStatisticQueryResult result = assessmentStatisticsService.query(assessmentSubmission.getAssessment().getId());

		// 이게 첫번째 제출인 경우
		if (assessmentSubmission.getIsFirstSubmission()) {
			final Integer elapsedTimeRank = getElapsedTimeRank(result.ascendingElapsedTimes(),
				assessmentSubmission.getElapsedTime());
			final Integer scoreRank = getScoreRank(result.descendingScores(), assessmentSubmission.getTotalScore());

			return new AssessmentSubmissionRankResult(
				result.descendingScores(),
				assessmentSubmission.getTotalScore(),
				scoreRank == null ? result.descendingScores().size() : scoreRank,
				result.ascendingElapsedTimes(),
				assessmentSubmission.getElapsedTime(),
				elapsedTimeRank == null ? result.descendingScores().size() : elapsedTimeRank,
				result.ascendingElapsedTimes().size()
			);
		}

		// 이게 첫번째 제출이 아닌 경우
		final Integer elapsedTimeRank = getElapsedTimeRank(result.ascendingElapsedTimes(),
			assessmentSubmission.getElapsedTime());
		final Integer scoreRank = getScoreRank(result.descendingScores(), assessmentSubmission.getTotalScore());

		return new AssessmentSubmissionRankResult(
			result.descendingScores(),
			assessmentSubmission.getTotalScore(),
			scoreRank == null ? result.descendingScores().size() + 1 : scoreRank,
			result.ascendingElapsedTimes(),
			assessmentSubmission.getElapsedTime(),
			elapsedTimeRank == null ? result.descendingScores().size() + 1 : elapsedTimeRank,
			result.descendingScores().size() + 1
		);
	}

	private Integer getElapsedTimeRank(final List<Duration> ascendingDurations, final Duration elapsedTime) {
		for (int i = 0; i < ascendingDurations.size(); i++) {
			final Duration currentDuration = ascendingDurations.get(i);

			if (currentDuration.compareTo(elapsedTime) >= 0) {
				return i + 1;
			}
		}

		// 들어갈 위치 찾을 수 없음
		return null;
	}

	private Integer getScoreRank(final List<Integer> descendingScores, final int score) {
		for (int i = 0; i < descendingScores.size(); i++) {
			final int currentScore = descendingScores.get(i);

			if (currentScore <= score) {
				return i + 1;
			}
		}

		// 들어갈 위치 찾을 수 없음
		return null;
	}
}
