package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentAttemptResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttempt;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentAttemptRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AssessmentAttemptStartManager {
	private final AssessmentRepository assessmentRepository;
	private final AssessmentAttemptRepository assessmentAttemptRepository;
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;
	private final Clock assessmentClock;

	@Transactional
	public AssessmentAttemptResult start(final Long assessmentId, final Long memberId) {
		final Assessment assessment = assessmentRepository.findById(assessmentId)
			.orElseThrow(NoSuchAssessmentException::new);
		final Instant now = assessmentClock.instant();
		final String activeKey = AssessmentAttempt.activeKey(assessmentId, memberId);

		final Optional<AssessmentAttempt> activeAttempt =
			assessmentAttemptRepository.findByActiveKeyForUpdate(activeKey);
		if (activeAttempt.isPresent() && !activeAttempt.get().isExpiredAt(now)) {
			return result(activeAttempt.get(), now, false);
		}
		if (activeAttempt.isPresent()) {
			activeAttempt.get().expire();
			assessmentAttemptRepository.flush();
		}

		final int attemptNumber = assessmentAttemptRepository
			.findTopByAssessmentIdAndMemberIdOrderByAttemptNumberDesc(assessmentId, memberId)
			.map(previous -> previous.getAttemptNumber() + 1)
			.orElse(1);
		final AssessmentAttempt attempt = AssessmentAttempt.start(
			assessmentId,
			memberId,
			attemptNumber,
			now,
			now.plus(assessment.getAnswerInputDelay()),
			now.plus(assessment.getAssessmentDuration())
		);
		assessmentAttemptRepository.saveAndFlush(attempt);
		return result(attempt, now, true);
	}

	@Transactional
	public Optional<AssessmentAttemptResult> getActive(final Long assessmentId, final Long memberId) {
		final Instant now = assessmentClock.instant();
		final Optional<AssessmentAttempt> activeAttempt = assessmentAttemptRepository.findByActiveKeyForUpdate(
			AssessmentAttempt.activeKey(assessmentId, memberId));
		if (activeAttempt.isEmpty()) {
			return Optional.empty();
		}
		if (activeAttempt.get().isExpiredAt(now)) {
			activeAttempt.get().expire();
			return Optional.empty();
		}
		return Optional.of(result(activeAttempt.get(), now, false));
	}

	private AssessmentAttemptResult result(
		final AssessmentAttempt attempt,
		final Instant now,
		final boolean newlyCreated
	) {
		final boolean rankEligible = !assessmentSubmissionRepository.existsByAssessmentIdAndMemberId(
			attempt.getAssessmentId(), attempt.getMemberId());
		return AssessmentAttemptResult.from(attempt, now, rankEligible, newlyCreated);
	}
}
