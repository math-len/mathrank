package kr.co.mathrank.domain.problem.assessment.dto;

import java.util.List;

public record SubmissionQueryResults(
	List<SubmissionQueryResult> queryResults
) {
}
