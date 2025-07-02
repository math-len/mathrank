package kr.co.mathrank.domain.problem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemRepository problemRepository;
	private final Snowflake snowflake;

	public Long save(@NotNull @Valid final ProblemRegisterCommand command) {
		final Long id = snowflake.nextId();
		final Problem problem = Problem.of(id, command.requestMemberId(), command.imageSource(), command.difficulty(),
			command.answerType(), command.problemCourse(), command.answer(), command.schoolCode());

		problemRepository.save(problem);
		return id;
	}

	@Transactional
	public void update(@NotNull @Valid final ProblemUpdateCommand command) {
		final Problem problem = problemRepository.findById(command.problemId())
			.orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + command.problemId()));

		if (!command.requestMemberId().equals(problem.getMemberId())) {
			throw new IllegalArgumentException("Member ID does not match the problem owner.");
		}

		problem.setDifficulty(command.difficulty());
		problem.setProblemCourse(command.problemCourse());
		problem.setType(command.answerType());
		problem.setAnswer(command.answer());
		problem.setSchoolCode(command.schoolCode());
		problem.setImageSource(command.imageSource());
	}
}
