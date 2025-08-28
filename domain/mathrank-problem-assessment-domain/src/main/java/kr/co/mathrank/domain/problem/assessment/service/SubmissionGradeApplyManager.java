package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.EvaluationStatus;
import kr.co.mathrank.domain.problem.assessment.entity.GradeResult;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchSubmissionException;
import kr.co.mathrank.domain.problem.assessment.exception.SubmissionGradeException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class SubmissionGradeApplyManager {
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;

	@Transactional
	public void applyGrade(final List<GradeResult> gradeResults, final Long submissionId) {
		// 이미 현재 submission이 적용됐는지 확인 ( X-Lock )
		final AssessmentSubmission submission = assessmentSubmissionRepository.findByIdWithSubmittedItemAnswersForUpdate(submissionId)
			.orElseThrow(() -> {
				log.warn("[SubmissionGradeManager.evaluateSubmission] cannot found assessmentSubmission - assessmentSubmissionId: {}", submissionId);
				return new NoSuchSubmissionException();
			});

		// 이미 채점된 제출
		if (submission.getEvaluationStatus() == EvaluationStatus.FINISHED) {
			log.info("[SubmissionGradeManager.evaluateSubmission] already applied submission - submissionId: {}", submissionId);
			throw new SubmissionGradeException();
		}

		submission.grade(gradeResults);
	}
}
