package kr.co.mathrank.domain.problem.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.problem.dto.ProblemDeleteCommand;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.entity.Answer;
import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.exception.CannotAccessProblemException;
import kr.co.mathrank.domain.problem.exception.CannotFoundCourseException;
import kr.co.mathrank.domain.problem.exception.CannotFoundProblemException;
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
		final Course course = getCourse(command.coursePath());

		final Long id = snowflake.nextId();
		final Problem problem = Problem.of(id, command.requestMemberId(), command.imageSource(), command.difficulty(),
			command.answerType(), course, command.schoolCode(), command.solutionVideoLink(), command.solutionImage(), command.year());
		final Set<Answer> answers = mapToAnswer(command.answers(), problem);
		problem.setAnswers(answers);

		problemRepository.save(problem);
		return id;
	}

	@Transactional
	public void update(@NotNull @Valid final ProblemUpdateCommand command) {
		final Problem problem = problemRepository.findById(command.problemId())
			.orElseThrow(() -> new CannotFoundProblemException(command.problemId()));
		final Course course = getCourse(command.coursePath());

		isOwner(command.requestMemberId(), problem);

		problem.setDifficulty(command.difficulty());
		problem.setCourse(course);
		problem.setType(command.answerType());
		problem.setAnswers(mapToAnswer(command.answers(), problem));
		problem.setSchoolCode(command.schoolCode());
		problem.setImageSource(command.imageSource());
	}

	private Course getCourse(String command) {
		return courseRepository.findById(new Path(command))
			.orElseThrow(() -> new CannotFoundCourseException(command));
	}

	@Transactional
	public void delete(@NotNull @Valid final ProblemDeleteCommand command) {
		final Problem problem = problemRepository.findById(command.problemId())
			.orElseThrow(() -> new CannotFoundProblemException(command.problemId()));

		isOwner(command.requestMemberId(), problem);

		log.debug("[ProblemService.delete] Deleting problem with ID: {}", command.problemId());
		problemRepository.delete(problem);
	}

	private void isOwner(final Long requestMemberId, final Problem problem) {
		if (!requestMemberId.equals(problem.getMemberId())) {
			throw new CannotAccessProblemException();
		}
	}

	// 중복 제거
	private Set<Answer> mapToAnswer(final Set<String> answers, final Problem problem) {
		return answers.stream()
			.map(String::trim)
			.map(answer -> Answer.of(snowflake.nextId(), answer, problem))
			.collect(Collectors.toSet());
	}
}
