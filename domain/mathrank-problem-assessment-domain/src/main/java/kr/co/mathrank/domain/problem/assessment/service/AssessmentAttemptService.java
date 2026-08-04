package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentAttemptResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentAttemptSubmissionCommand;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttempt;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentAttemptStatus;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentAttemptException;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentAttemptRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AssessmentAttemptService {
	private static final int MAX_SHORT_ANSWERS = 100;

	private final AssessmentAttemptStartManager assessmentAttemptStartManager;
	private final AssessmentAttemptRepository assessmentAttemptRepository;
	private final AssessmentRepository assessmentRepository;
	private final SubmissionRegisterService submissionRegisterService;
	private final ProblemQueryManager problemQueryManager;
	private final Clock assessmentClock;

	public AssessmentAttemptResult start(@NotNull final Long assessmentId, @NotNull final Long memberId) {
		try {
			return assessmentAttemptStartManager.start(assessmentId, memberId);
		} catch (DataIntegrityViolationException exception) {
			return assessmentAttemptStartManager.getActive(assessmentId, memberId)
				.orElseThrow(() -> exception);
		}
	}

	public Optional<AssessmentAttemptResult> getActive(
		@NotNull final Long assessmentId,
		@NotNull final Long memberId
	) {
		return assessmentAttemptStartManager.getActive(assessmentId, memberId);
	}

	@Transactional
	public Long submit(@NotNull @Valid final AssessmentAttemptSubmissionCommand command) {
		final AssessmentAttempt attempt = assessmentAttemptRepository.findByIdForUpdate(command.attemptId())
			.orElseThrow(AssessmentAttemptException::notFound);
		if (!attempt.getMemberId().equals(command.memberId())) {
			throw AssessmentAttemptException.accessDenied();
		}
		if (attempt.getStatus() == AssessmentAttemptStatus.SUBMITTED) {
			throw AssessmentAttemptException.alreadySubmitted();
		}
		if (attempt.getStatus() == AssessmentAttemptStatus.EXPIRED) {
			throw AssessmentAttemptException.expired();
		}

		final Instant now = assessmentClock.instant();
		if (attempt.isExpiredAt(now)) {
			attempt.expire();
			throw AssessmentAttemptException.expired();
		}
		if (!attempt.isAnswerInputEnabledAt(now)) {
			throw AssessmentAttemptException.answerLocked();
		}

		final Assessment assessment = assessmentRepository.findWithItems(attempt.getAssessmentId())
			.orElseThrow(NoSuchAssessmentException::new);
		final List<List<String>> normalizedAnswers = normalizeAnswers(assessment, command.submittedAnswers());
		final Duration elapsedTime = Duration.between(attempt.getStartedAt(), now);
		final Long submissionId = submissionRegisterService.submitFromAttempt(new SubmissionRegisterCommand(
			command.memberId(),
			attempt.getAssessmentId(),
			normalizedAnswers,
			elapsedTime
		));
		attempt.submit(submissionId);
		return submissionId;
	}

	private List<List<String>> normalizeAnswers(
		final Assessment assessment,
		final List<List<String>> submittedAnswers
	) {
		if (assessment.getAssessmentItems().size() != submittedAnswers.size()) {
			throw AssessmentAttemptException.invalidAnswer();
		}

		final List<List<String>> normalizedAnswers = new ArrayList<>(submittedAnswers.size());
		for (int index = 0; index < submittedAnswers.size(); index++) {
			final AssessmentItem item = assessment.getAssessmentItems().get(index);
			final ProblemQueryResult problem = problemQueryManager.getProblemInfo(item.getProblemId());
			if (submittedAnswers.get(index) == null
				|| submittedAnswers.get(index).stream().anyMatch(answer -> answer == null)) {
				throw AssessmentAttemptException.invalidAnswer();
			}
			final List<String> normalized = submittedAnswers.get(index).stream()
				.map(String::trim)
				.filter(answer -> !answer.isEmpty())
				.toList();
			if (new HashSet<>(normalized).size() != normalized.size()) {
				throw AssessmentAttemptException.invalidAnswer();
			}
			switch (problem.type()) {
				case MULTIPLE_CHOICE -> validateMultipleChoice(normalized);
				case SHORT_ANSWER -> {
					if (normalized.size() > MAX_SHORT_ANSWERS) {
						throw AssessmentAttemptException.invalidAnswer();
					}
				}
			}
			normalizedAnswers.add(normalized);
		}
		return normalizedAnswers;
	}

	private void validateMultipleChoice(final List<String> answers) {
		if (answers.size() > 5) {
			throw AssessmentAttemptException.invalidAnswer();
		}
		if (answers.stream().anyMatch(answer -> !answer.matches("[1-5]"))) {
			throw AssessmentAttemptException.invalidAnswer();
		}
	}
}
