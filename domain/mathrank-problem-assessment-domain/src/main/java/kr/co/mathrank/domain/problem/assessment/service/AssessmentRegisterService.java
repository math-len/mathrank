package kr.co.mathrank.domain.problem.assessment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.LimitedAssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentPeriodType;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentRegisterException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AssessmentRegisterService {
	private final AssessmentRepository assessmentRepository;
	private final TransactionalOutboxPublisher transactionalOutboxPublisher;

	@Transactional
	public Long register(@NotNull @Valid final AssessmentRegisterCommand command) {
		final Assessment assessment = Assessment.unlimited(command.registerMemberId(), command.assessmentName(), command.minutes());

		return register(assessment, command.role(), toItems(command.assessmentItems())).getId();
	}

	@Transactional
	public Long registerLimited(@NotNull @Valid final LimitedAssessmentRegisterCommand command) {
		final Assessment assessment = Assessment.limited(command.registerMemberId(), command.assessmentName(),
			command.minutes(), command.startAt(), command.endAt());

		return register(assessment, command.role(), toItems(command.assessmentItems())).getId();
	}

	private Assessment register(final Assessment assessment, final Role role, final List<AssessmentItem> assessmentItems) {
		// 관리자가 아닐 경우 에러
		if (!role.equals(Role.ADMIN)) {
			log.warn("[AssessmentRegisterService.register] cannot register assessment - requestMember role: {}", role);
			throw new AssessmentRegisterException();
		}

		assessment.replaceItems(assessmentItems);
		assessmentRepository.save(assessment);
		log.info("[AssessmentRegisterService.register] successfully registered assessment - assessmentId: {}", assessment.getId());

		transactionalOutboxPublisher.publish("assessment-registered", AssessmentRegisteredEvent.from(assessment));
		return assessment;
	}

	private List<AssessmentItem> toItems(final List<AssessmentItemRegisterCommand> commands) {
		return commands.stream()
			.map(item -> AssessmentItem.of(item.problemId(), item.score()))
			.toList();
	}

	record AssessmentRegisteredEvent(
		Long assessmentId,
		Long registeredMemberId,
		String assessmentName,
		Long assessmentMinutes,
		List<Long> assessmentItemProblemIds,
		LocalDateTime createdAt,
		AssessmentPeriodType periodType,
		LocalDateTime startAt,
		LocalDateTime endAt
	) implements EventPayload {
		static AssessmentRegisteredEvent from(final Assessment assessment) {
			return new AssessmentRegisteredEvent(
				assessment.getId(),
				assessment.getRegisterMemberId(),
				assessment.getAssessmentName(),
				assessment.getAssessmentDuration().toMinutes(),
				assessment.getAssessmentItems().stream()
					.map(AssessmentItem::getProblemId)
					.toList(),
				assessment.getCreatedAt(),
				assessment.getAssessmentSubmissionPeriod().getPeriodType(),
				assessment.getAssessmentSubmissionPeriod().getStartAt(),
				assessment.getAssessmentSubmissionPeriod().getEndAt()
			);
		}
	}
}
