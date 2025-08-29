package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;

public record SubmissionQueryResult(
	Long assessmentId,
	Long submissionId,
	Long assessmentAverageScore,
	Long memberId,
	EvaluationStatus evaluationStatus,
	Integer totalScore,
	List<SubmissionItemQueryResult> submissionItemQueryResults,
	LocalDateTime submittedAt,
	Duration elapsedTime
) {
	public static SubmissionQueryResult from(final AssessmentSubmission assessmentSubmission) {
		return new SubmissionQueryResult(
			assessmentSubmission.getAssessment().getId(),
			assessmentSubmission.getId(),
			assessmentSubmission.getAssessment().getAverageScore(),
			assessmentSubmission.getMemberId(),
			assessmentSubmission.getEvaluationStatus(),
			assessmentSubmission.getTotalScore(),
			assessmentSubmission.getSubmittedItemAnswers().stream()
				.map(SubmissionItemQueryResult::from)
				.sorted(Comparator.comparingInt(SubmissionItemQueryResult::sequence))
				.toList(),
			assessmentSubmission.getSubmittedAt(),
			assessmentSubmission.getElapsedTime()
		);
	}
}
