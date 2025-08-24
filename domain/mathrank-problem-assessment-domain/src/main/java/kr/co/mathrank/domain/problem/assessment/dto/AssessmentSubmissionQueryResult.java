package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;

public record AssessmentSubmissionQueryResult(
	Long assessmentId,
	Long memberId,
	EvaluationStatus evaluationStatus,
	Integer totalScore,
	List<ItemSubmissionResult> itemSubmissionResults,
	LocalDateTime submittedAt,
	Duration elapsedTime
) {
	public static AssessmentSubmissionQueryResult from(final AssessmentSubmission assessmentSubmission) {
		return new AssessmentSubmissionQueryResult(
			assessmentSubmission.getAssessment().getId(),
			assessmentSubmission.getMemberId(),
			assessmentSubmission.getEvaluationStatus(),
			assessmentSubmission.getTotalScore(),
			assessmentSubmission.getSubmittedItemAnswers().stream()
				.map(ItemSubmissionResult::from)
				.sorted(Comparator.comparingInt(ItemSubmissionResult::sequence))
				.toList(),
			assessmentSubmission.getSubmittedAt(),
			assessmentSubmission.getElapsedTime()
		);
	}
}
