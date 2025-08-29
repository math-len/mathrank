package kr.co.mathrank.domain.problem.assessment.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionQueryResult;
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

	public SubmissionQueryResult getSubmissionResult(@NotNull final Long submissionId) {
		final AssessmentSubmission submission = assessmentSubmissionRepository.findByIdWithSubmittedItemAnswers(submissionId)
			.orElseThrow(() -> {
				log.info("[SubmissionQueryService.getSubmissionResult] cannot find submission - submissionId: {}", submissionId);
				return new NoSuchSubmissionException();
			});
		return SubmissionQueryResult.from(submission);
	}
}
