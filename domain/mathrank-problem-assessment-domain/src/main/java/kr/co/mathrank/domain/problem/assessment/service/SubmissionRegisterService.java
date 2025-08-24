package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SubmissionRegisterService {
	private final AssessmentRepository assessmentRepository;
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;

	private final TransactionalOutboxPublisher transactionalOutboxPublisher;

	@Transactional
	public Long submit(@NotNull @Valid final SubmissionRegisterCommand command) {
		final Assessment assessment = assessmentRepository.findWithItems(command.assessmentId())
			.orElseThrow(() -> {
				log.info("[SubmissionRegisterService.submit] cannot found assessment - assessmentId: {}",
					command.assessmentId());
				return new NoSuchAssessmentException();
			});
		final AssessmentSubmission assessmentSubmission = assessment.registerSubmission(
			command.memberId(),
			command.submittedAnswers(),
			command.elapsedTime()
		);
		assessmentSubmissionRepository.save(assessmentSubmission);

		transactionalOutboxPublisher.publish("assessment-submission-registered", new SubmissionRegisteredEvent(
			assessment.getId(),
			command.memberId(),
			assessmentSubmission.getId(),
			assessmentSubmission.getSubmittedAt(),
			assessmentSubmission.getElapsedTime().getSeconds()
		));

		return assessmentSubmission.getId();
	}

	record SubmissionRegisteredEvent(
		Long assessmentId,
		Long memberId,
		Long submissionId,
		LocalDateTime submittedTime,
		Long elapsedTimeSeconds
	) implements EventPayload {
	}
}
