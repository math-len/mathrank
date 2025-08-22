package kr.co.mathrank.domain.problem.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.problem.dto.ProblemDeleteCommand;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.entity.Answer;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.exception.CannotAccessProblemException;
import kr.co.mathrank.domain.problem.exception.CannotFoundProblemException;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemRepository problemRepository;
	private final Snowflake snowflake;
	private final SchoolLocationManager schoolLocationManager;
	private final TransactionalOutboxPublisher transactionalOutboxPublisher;
	private final ProblemUpdateManager problemUpdateManager;

	public Long save(@NotNull @Valid final ProblemRegisterCommand command) {

		final Long id = snowflake.nextId();
		final Problem problem = Problem.of(
			id,
			command.requestMemberId(),
			command.imageSource(),
			command.difficulty(),
			command.answerType(),
			command.coursePath(),
command.schoolCode(),
			command.solutionVideoLink(),
			command.solutionImage(),
			command.year(),
			command.schoolCode() == null ? null : schoolLocationManager.getSchoolLocation(command.schoolCode()),
			command.memo()
		);
		final Set<Answer> answers = mapToAnswer(command.answers(), problem);
		problem.setAnswers(answers);

		problemRepository.save(problem);
		log.info("[ProblemService.save] problem created - id: {}, memberId: {}, course: {}",
			id, command.requestMemberId(), command.coursePath());
		return id;
	}

	public void update(@NotNull @Valid final ProblemUpdateCommand command) {
		final String schoolLocation = schoolLocationManager.getSchoolLocation(command.schoolCode());
		problemUpdateManager.update(command, schoolLocation);
	}

	@Transactional
	public void delete(@NotNull @Valid final ProblemDeleteCommand command) {
		final Problem problem = problemRepository.findById(command.problemId())
			.orElseThrow(() -> new CannotFoundProblemException(command.problemId()));

		isOwner(command.requestMemberId(), problem);

		log.info("[ProblemService.delete] problem deleted - id: {}, memberId: {}",
			command.problemId(), command.requestMemberId());
		problemRepository.delete(problem);
	}

	private void isOwner(final Long requestMemberId, final Problem problem) {
		if (!requestMemberId.equals(problem.getMemberId())) {
			log.warn("[Problem.isOwner] is not owner - problemId: {}, ownerId: {}, requestMemberId: {}",
				problem.getId(), problem.getMemberId(), requestMemberId);
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
