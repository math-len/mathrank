package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionRankResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionStatisticQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.assessment.exception.SubmissionGradeException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AssessmentRankQueryService {
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;
	private final AssessmentStatisticsService assessmentStatisticsService;

	public AssessmentSubmissionRankResult getRank(final Long submissionId) {
		final AssessmentSubmission assessmentSubmission = assessmentSubmissionRepository.findById(submissionId)
			.orElseThrow();

		// 채점 안됐으면 불가
		if (assessmentSubmission.getEvaluationStatus() == EvaluationStatus.PENDING) {
			throw new SubmissionGradeException();
		}

		final AssessmentSubmissionStatisticQueryResult result = assessmentStatisticsService.query(assessmentSubmission.getAssessment().getId());
		final int elapsedTimeRank = getElapsedTimeRank(result.ascendingElapsedTimes(),
			assessmentSubmission.getElapsedTime());
		final int scoreRAnk = getScoreRank(result.descendingScores(), assessmentSubmission.getTotalScore());

		return new AssessmentSubmissionRankResult(
			result.descendingScores(),
			assessmentSubmission.getTotalScore(),
			scoreRAnk,
			result.ascendingElapsedTimes(),
			assessmentSubmission.getElapsedTime(),
			elapsedTimeRank
		);
	}

	private int getElapsedTimeRank(final List<Duration> ascendingDurations, final Duration elapsedTime) {
		for (int i = 0; i < ascendingDurations.size(); i++) {
			final Duration currentDuration = ascendingDurations.get(i);

			if (currentDuration.compareTo(elapsedTime) >= 0) {
				return i + 1;
			}
		}

		return ascendingDurations.size();
	}

	private int getScoreRank(final List<Integer> descendingScores, final int score) {
		for (int i = 0; i < descendingScores.size(); i++) {
			final int currentScore = descendingScores.get(i);

			if (currentScore <= score) {
				return i + 1;
			}
		}

		return descendingScores.size();
	}
}
