package kr.co.mathrank.domain.problem.assessment.dto;

import java.util.List;

public record AssessmentSubmissionQueryResults(
	List<SubmissionQueryResult> queryResults
) {
}
