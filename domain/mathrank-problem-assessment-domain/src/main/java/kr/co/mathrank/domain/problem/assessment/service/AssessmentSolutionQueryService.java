package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQuery;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.ProblemSolutionResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.exception.CannotGetSolutionException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AssessmentSolutionQueryService {
	private final AssessmentRepository assessmentRepository;
	private final ProblemQueryManager problemQueryManager;

	/**
	 * 사용자가 문제를 푼 경우에만 정답 조회가 가능합니다.
	 * @param query
	 */
	public AssessmentSolutionQueryResult querySolutions(@NotNull @Valid final AssessmentSolutionQuery query) {
		final Assessment solvedAssessment = getSubmittedAssessment(query.assessmentId(), query.requestMemberId());

		final List<ProblemSolutionResult> results = solvedAssessment.getAssessmentItems().stream()
			.map(AssessmentItem::getProblemId)
			.map(problemQueryManager::getProblemInfo)
			.map(ProblemSolutionResult::from)
			.toList();

		return new AssessmentSolutionQueryResult(results);
	}

	private Assessment getSubmittedAssessment(final Long assessmentId, final Long requestMemberId) {
		return assessmentRepository.findByAssessmentIdAndSubmissionMemberId(assessmentId, requestMemberId)
			.orElseThrow(() -> {
				log.info(
					"[AssessmentSolutionQueryService.querySolutions] assessment not solved - assessmentId: {}, requestMemberId: {}",
					assessmentId, requestMemberId);
				return new CannotGetSolutionException();
			});
	}
}
