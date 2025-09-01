package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionStatisticQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AssessmentStatisticsService {
	private final AssessmentRepository assessmentRepository;

	public AssessmentSubmissionStatisticQueryResult query(@NotNull final Long assessmentId) {
		final Assessment assessment = assessmentRepository.findWithSubmissions(assessmentId)
			.orElseThrow(() -> {
				log.info("[AssessmentStatisticsService.query] cannot found assessment - assessmentId: {}", assessmentId);
				return new NoSuchAssessmentException();
			});

		// 채점된 답안지만 비교한다.
		final List<AssessmentSubmission> submissions = getUsersFirstFinishedSubmissions(assessment);

		final List<Duration> ascendingDurations = ascendingSortElapsedTimes(submissions);
		final List<Integer> descendingScores = descendingSortScores(submissions);

		return new AssessmentSubmissionStatisticQueryResult(
			assessmentId,
			descendingScores,
			ascendingDurations
		);
	}

	private List<AssessmentSubmission> getUsersFirstFinishedSubmissions(final Assessment assessment) {
		return assessment.getAssessmentSubmissions()
			.stream()
			.filter(AssessmentSubmission::getIsFirstSubmission)
			.filter(assessmentSubmission -> assessmentSubmission.getEvaluationStatus() == EvaluationStatus.FINISHED)
			.toList();
	}

	private List<Integer> descendingSortScores(final List<AssessmentSubmission> submissions) {
		return submissions.stream()
			.sorted(Comparator.comparing(AssessmentSubmission::getTotalScore).reversed())
			.map(AssessmentSubmission::getTotalScore)
			.toList();
	}

	private List<Duration> ascendingSortElapsedTimes(final List<AssessmentSubmission> submissions) {
		return submissions.stream()
			.sorted(Comparator.comparing(AssessmentSubmission::getElapsedTime))
			.map(AssessmentSubmission::getElapsedTime)
			.toList();
	}
}
