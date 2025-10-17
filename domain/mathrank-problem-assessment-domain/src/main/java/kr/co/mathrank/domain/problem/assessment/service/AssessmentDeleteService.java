package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDeleteCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.exception.CannotDeleteAssessmentException;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentItemRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class AssessmentDeleteService {
	private final AssessmentRepository assessmentRepository;
	private final AssessmentItemRepository assessmentItemRepository;

	@Transactional
	public void delete(@NotNull @Valid final AssessmentDeleteCommand command) {
		final Assessment assessment = getAssessment(command.assessmentId());
		validateOwner(assessment, command.requestMemberId(), command.requestMemberRole());
		assessmentRepository.delete(assessment);
	}

	@Transactional
	public void deleteByProblemId(@NotNull final Long problemId) {
		final List<AssessmentItem> assessments = assessmentItemRepository.findAllContainsProblemId(problemId);
		assessmentRepository.deleteAll(assessments.stream()
			.map(AssessmentItem::getAssessment)
			.toList());
		log.info("[AssessmentDeleteService.deleteByProblemId] deleted assessment related with problemId - problemId: {}, assessmentCount: {}", problemId, assessments.size());
	}

	private Assessment getAssessment(final Long assessmentId) {
		return assessmentRepository.findById(assessmentId)
			.orElseThrow(() -> {
				log.info("[AssessmentDeleteService.getAssessment] cannot found assessment - assessmentId: {}",
					assessmentId);
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
