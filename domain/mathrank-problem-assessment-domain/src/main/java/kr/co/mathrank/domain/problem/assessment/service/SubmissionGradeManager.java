package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItemSubmission;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentSubmissionException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자의 시험 제출을 채점하는 매니저 컴포넌트입니다.
 */
@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class SubmissionGradeManager {
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;
	private final ProblemClient problemClient;

	/**
	 * 특정 시험 제출을 채점합니다.
	 *
	 * <p>제출 ID에 해당하는 {@link AssessmentSubmission}을 조회하고,
	 * 내부의 모든 {@link AssessmentItemSubmission}에 대해 {@link #checkAnswer(AssessmentItemSubmission)}를 호출하여
	 * 정답 여부를 평가합니다. 채점된 제출은 저장소에 다시 반영됩니다.</p>
	 *
	 * @param assessmentSubmissionId 채점할 시험 제출의 ID (null일 수 없음)
	 * @throws kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentSubmissionException 제출 ID에 해당하는 응시 기록이 없을 경우
	 */
	public void evaluateSubmission(@NotNull final Long assessmentSubmissionId) {
		final AssessmentSubmission submission = assessmentSubmissionRepository.findByAssessmentSubmissionId(assessmentSubmissionId)
			.orElseThrow(() -> {
				log.warn("[SubmissionGradeManager.evaluateSubmission] cannot found assessmentSubmission - assessmentSubmissionId: {}", assessmentSubmissionId);
				return new NoSuchAssessmentSubmissionException();
			});

		// 채점하기
		final List<AssessmentItemSubmission> itemSubmissions = submission.getSubmittedItemAnswers();
		itemSubmissions.forEach(this::checkAnswer);

		assessmentSubmissionRepository.save(submission);
	}

	/**
	 * 제출된 정답을 확인하고, 오답 여부를 기록한다.
	 * @param itemSubmission
	 */
	private void checkAnswer(final AssessmentItemSubmission itemSubmission) {
		final Long problemId = itemSubmission.getAssessmentItem().getProblemId();
		final SolveResult solveResult = problemClient.matchAnswer(problemId, itemSubmission.getSubmittedAnswer());

		itemSubmission.grade(solveResult.success(), solveResult.realAnswer().stream().toList());
	}
}
