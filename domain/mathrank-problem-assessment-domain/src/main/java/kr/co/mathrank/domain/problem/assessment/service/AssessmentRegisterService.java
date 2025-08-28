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
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
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
		// 관리자가 아닐 경우 에러
		if (!command.role().equals(Role.ADMIN)) {
			log.warn("[AssessmentRegisterService.register] cannot register assessment - command: {}", command);
			throw new AssessmentRegisterException();
		}
		// 관리자만 문제집 등록이 가능하다
		final Assessment assessment = Assessment.of(command.registerMemberId(), command.assessmentName(), command.minutes());
		final List<AssessmentItem> items = toItems(command.assessmentItems());
		assessment.replaceItems(items);
		assessmentRepository.save(assessment);
		log.info("[AssessmentRegisterService.register] successfully registered assessment - assessmentId: {}, command: {}", assessment.getId(), command);

		transactionalOutboxPublisher.publish("assessment-registered", AssessmentRegisteredEvent.from(assessment));
		return assessment.getId();
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
		LocalDateTime createdAt
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
				assessment.getCreatedAt()
			);
		}
	}
}
