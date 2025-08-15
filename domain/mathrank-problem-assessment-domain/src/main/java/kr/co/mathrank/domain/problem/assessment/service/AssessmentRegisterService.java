package kr.co.mathrank.domain.problem.assessment.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
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

	public Long register(@NotNull @Valid final AssessmentRegisterCommand command) {
		// 관리자가 아닐 경우 에러
		if (!command.role().equals(Role.ADMIN)) {
			log.warn("[AssessmentRegisterService.register] cannot register assessment - command: {}", command);
			throw new AssessmentRegisterException();
		}
		// 관리자만 문제집 등록이 가능하다
		final Assessment assessment = Assessment.of(command.registerMemberId(), command.assessmentName(), command.minutes());
		assessment.setAssessmentItems(command.problemIds());

		assessmentRepository.save(assessment);
		log.info("[AssessmentRegisterService.register] successfully registered assessment - assessmentId: {}, command: {}", assessment.getId(), command);
		return assessment.getId();
	}
}
