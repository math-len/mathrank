package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.GradeResult;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchSubmissionException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자의 시험 제출을 채점하는 매니저 컴포넌트입니다.
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SubmissionGradeService {
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;
	private final AssessmentRepository assessmentRepository;
	private final ItemGradeManager gradeManager;

	/**
	 * 지정된 제출(assessmentSubmissionId)을 채점합니다.
	 *
	 * @param assessmentSubmissionId 채점 대상 제출 ID (null 불가)
	 * @throws NoSuchSubmissionException 제출이 존재하지 않는 경우
	 */
	public void evaluateSubmission(@NotNull final Long assessmentSubmissionId) {
		final AssessmentSubmission submission = assessmentSubmissionRepository.findByIdWithSubmittedItemAnswers(assessmentSubmissionId)
			.orElseThrow(() -> {
				log.warn("[SubmissionGradeManager.evaluateSubmission] cannot found assessmentSubmission - assessmentSubmissionId: {}", assessmentSubmissionId);
				return new NoSuchSubmissionException();
			});

		// 채점하기
		this.gradeAll(submission);

		assessmentRepository.save(submission.getAssessment());
	}

	/**
	 * 제출된 정답을 확인하고, 오답 여부를 기록한다.
	 * @param assessmentSubmission
	 */
	private void gradeAll(final AssessmentSubmission assessmentSubmission) {
		final List<GradeResult> gradeResult = assessmentSubmission.getSubmittedItemAnswers().stream()
			.map(itemSubmission -> gradeManager.gradeItemSubmission(
				itemSubmission.getAssessmentItem().getProblemId(),
				itemSubmission.getSubmittedAnswer())
			)
			.toList();

		assessmentSubmission.grade(gradeResult);
	}
}
