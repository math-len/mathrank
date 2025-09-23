package kr.co.mathrank.domain.problem.assessment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentUpdateCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AssessmentUpdateService {
	private final AssessmentRepository assessmentRepository;

	@Transactional
	public void update(@NotNull @Valid final AssessmentUpdateCommand command) {
		final Assessment assessment = getAssessment(command.assessmentId());
		assessment.setAssessmentName(command.assessmentName());
		log.info("[AssessmentUpdateService.update] updated assessment - assessmentId: {}", assessment.getId());
	}

	private Assessment getAssessment(final Long assessmentId) {
		return assessmentRepository.findById(assessmentId)
			.orElseThrow(() -> {
				log.info("[AssessmentUpdateService.getAssessment] cannot found assessment - assessmentId: {}",
					assessmentId);
				return new NoSuchAssessmentException();
			});
	}
}
