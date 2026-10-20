package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttempt;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentAttemptRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 시간 제한이 없는 응시를 시작 3시간 뒤 임시 저장 답안으로 제출한다.
 * 스케줄러와 API 요청이 동시에 처리해도 응시 행을 잠근 뒤 상태를 다시 확인하므로 한 번만 제출된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
class AssessmentAutoSubmitManager {
	private final AssessmentAttemptRepository assessmentAttemptRepository;
	private final AssessmentRepository assessmentRepository;
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;
	private final SubmissionRegisterService submissionRegisterService;
	private final ProblemQueryManager problemQueryManager;
	private final Clock assessmentClock;

	@Transactional
	public boolean autoSubmitIfDue(final Long attemptId) {
		final Optional<AssessmentAttempt> found = assessmentAttemptRepository.findByIdForUpdate(attemptId);
		if (found.isEmpty() || !found.get().isAutoSubmitDueAt(assessmentClock.instant())) {
			return false;
		}
		final AssessmentAttempt attempt = found.get();

		// 1인 1회 정책 이전에 이미 제출한 회원의 응시는 제출하지 않고 닫는다.
		if (assessmentSubmissionRepository.existsByAssessmentIdAndMemberId(
			attempt.getAssessmentId(), attempt.getMemberId())) {
			attempt.expire();
			return false;
		}

		final Assessment assessment = assessmentRepository.findWithItems(attempt.getAssessmentId()).orElse(null);
		if (assessment == null) {
			attempt.expire();
			return false;
		}

		final List<List<String>> answers = sanitize(
			assessment, attempt.draftAnswersFor(assessment.getAssessmentItems().size()));
		final Long submissionId = submissionRegisterService.submitFromAttempt(new SubmissionRegisterCommand(
			attempt.getMemberId(),
			attempt.getAssessmentId(),
			answers,
			AssessmentAttempt.AUTO_SUBMIT_AFTER
		));
		attempt.submit(submissionId);
		log.info("[AssessmentAutoSubmitManager] auto submitted - attemptId: {}, submissionId: {}",
			attemptId, submissionId);
		return true;
	}

	@Transactional(readOnly = true)
	public List<Long> findDueAttemptIds(final int limit) {
		final Instant threshold = assessmentClock.instant().minus(AssessmentAttempt.AUTO_SUBMIT_AFTER);
		return assessmentAttemptRepository.findAutoSubmitDueIds(threshold, PageRequest.of(0, limit));
	}

	/** 임시 저장 답안은 검증 없이 저장되므로, 제출 전에 문항 유형에 맞지 않는 값은 버린다. */
	private List<List<String>> sanitize(final Assessment assessment, final List<List<String>> drafts) {
		final List<List<String>> sanitized = new ArrayList<>(drafts.size());
		for (int index = 0; index < drafts.size(); index++) {
			final AssessmentItem item = assessment.getAssessmentItems().get(index);
			final List<String> distinct = new ArrayList<>(new HashSet<>(drafts.get(index).stream()
				.filter(answer -> answer != null)
				.map(String::trim)
				.filter(answer -> !answer.isEmpty())
				.toList()));
			final ProblemQueryResult problem = problemQueryManager.getProblemInfo(item.getProblemId());
			sanitized.add(switch (problem.type()) {
				case MULTIPLE_CHOICE -> distinct.stream().filter(answer -> answer.matches("[1-5]")).sorted().toList();
				case SHORT_ANSWER -> distinct.stream().limit(100).toList();
			});
		}
		return sanitized;
	}
}
