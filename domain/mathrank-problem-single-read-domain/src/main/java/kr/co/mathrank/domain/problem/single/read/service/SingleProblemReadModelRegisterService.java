package kr.co.mathrank.domain.problem.single.read.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelRegisterCommand;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.exception.SingleProblemReadModelAlreadyExistException;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemReadModelRegisterService {
	private final SingleProblemReadModelRepository singleProblemReadModelRepository;

	public void save(@NotNull @Valid final SingleProblemReadModelRegisterCommand command) {
		final SingleProblemReadModel model = SingleProblemReadModel.of(command.singleProblemId(), command.problemId(),
			command.singleProblemName(), command.problemImage(), command.coursePath(), command.answerType(),
			command.difficulty(), 0L, 0L, 0L,
			command.createdAt());
		model.setLocation(command.location());

		singleProblemReadModelRepository.save(model);
	}
}
