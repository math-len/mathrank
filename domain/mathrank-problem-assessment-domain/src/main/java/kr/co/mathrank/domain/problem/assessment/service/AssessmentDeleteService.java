package kr.co.mathrank.domain.problem.assessment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDeleteCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentException;
import kr.co.mathrank.domain.problem.assessment.exception.CannotDeleteAssessmentException;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class AssessmentDeleteService {
	private final AssessmentRepository assessmentRepository;

	@Transactional
	public void delete(@NotNull @Valid final AssessmentDeleteCommand command) {
		final Assessment assessment = getAssessment(command);
		validateOwner(assessment, command.requestMemberId(), command.requestMemberRole());
		assessmentRepository.delete(assessment);
	}

	private Assessment getAssessment(AssessmentDeleteCommand command) {
		return assessmentRepository.findById(command.assessmentId())
			.orElseThrow(() -> {
				log.info("[AssessmentDeleteService.getAssessment] cannot found assessment - assessmentId: {}",
					command.assessmentId());
				return new NoSuchAssessmentException();
			});
	}

	private void validateOwner(final Assessment assessment, final Long requestMemberId, final Role requestMemberRole) {
		if (requestMemberRole == Role.ADMIN) {
			return;
		}

		if (assessment.getRegisterMemberId().equals(requestMemberId)) {
			return;
		}

		throw new CannotDeleteAssessmentException();
	}
}
