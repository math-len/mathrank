package kr.co.mathrank.domain.problem.assessment.service;

import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionQueryResults;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchSubmissionException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SubmissionQueryService {
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;

	public AssessmentSubmissionQueryResult getSubmissionResult(@NotNull final Long submissionId) {
		final AssessmentSubmission submission = assessmentSubmissionRepository.findByIdWithSubmittedItemAnswers(submissionId)
			.orElseThrow(() -> {
				log.info("[SubmissionQueryService.getSubmissionResult] cannot find submission - submissionId: {}", submissionId);
				return new NoSuchSubmissionException();
			});
		return AssessmentSubmissionQueryResult.from(submission);
	}

	public AssessmentSubmissionQueryResults getAssessmentSubmissionResults(
		@NotNull final Long assessmentId,
		@NotNull final Long memberId
	) {
		return new AssessmentSubmissionQueryResults(
			assessmentSubmissionRepository.findAllByAssessmentIdAndMemberId(assessmentId, memberId)
				.stream()
				.map(AssessmentSubmissionQueryResult::from)
				.sorted(Comparator.comparing(AssessmentSubmissionQueryResult::submittedAt).reversed())
				.toList());
	}
}
