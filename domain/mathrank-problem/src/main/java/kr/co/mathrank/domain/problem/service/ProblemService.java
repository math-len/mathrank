package kr.co.mathrank.domain.problem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.problem.dto.ProblemDeleteCommand;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.repository.CourseRepository;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemRepository problemRepository;
	private final CourseRepository courseRepository;
	private final Snowflake snowflake;

	public Long save(@NotNull @Valid final ProblemRegisterCommand command) {
		final Long id = snowflake.nextId();
		final Course course = getCourse(command.coursePath());

		final Problem problem = Problem.of(id, command.requestMemberId(), command.imageSource(), command.difficulty(),
			command.answerType(), course, command.answer(), command.schoolCode());

		problemRepository.save(problem);
		return id;
	}

	@Transactional
	public void update(@NotNull @Valid final ProblemUpdateCommand command) {
		final Problem problem = problemRepository.findById(command.problemId())
			.orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + command.problemId()));
		final Course course = getCourse(command.coursePath());

		isOwner(command.requestMemberId(), problem);

		problem.setDifficulty(command.difficulty());
		problem.setCourse(course);
		problem.setType(command.answerType());
		problem.setAnswer(command.answer());
		problem.setSchoolCode(command.schoolCode());
		problem.setImageSource(command.imageSource());
	}

	private Course getCourse(String command) {
		return courseRepository.findById(new Path(command))
			.orElseThrow();
	}

	@Transactional
	public void delete(@NotNull @Valid final ProblemDeleteCommand command) {
		final Problem problem = problemRepository.findById(command.problemId())
			.orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + command.problemId()));

		isOwner(command.requestMemberId(), problem);

		log.debug("[ProblemService.delete] Deleting problem with ID: {}", command.problemId());
		problemRepository.delete(problem);
	}

	private void isOwner(final Long requestMemberId, final Problem problem) {
		if (!requestMemberId.equals(problem.getMemberId())) {
			throw new IllegalArgumentException("Member ID does not match the problem owner.");
		}
	}
}
