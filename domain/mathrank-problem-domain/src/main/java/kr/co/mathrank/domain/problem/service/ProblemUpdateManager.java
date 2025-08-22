package kr.co.mathrank.domain.problem.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.entity.Answer;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.exception.CannotAccessProblemException;
import kr.co.mathrank.domain.problem.exception.CannotFoundProblemException;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class ProblemUpdateManager {
	private final ProblemRepository problemRepository;
	private final Snowflake snowflake;
	private final TransactionalOutboxPublisher outboxPublisher;

	@Transactional
	public void update(final ProblemUpdateCommand command, final String schoolLocation) {
		final Problem problem = problemRepository.findById(command.problemId())
			.orElseThrow(() -> new CannotFoundProblemException(command.problemId()));

		isOwner(command.requestMemberId(), problem);

		problem.setDifficulty(command.difficulty());
		problem.setCoursePath(command.coursePath());
		problem.setType(command.answerType());
		problem.setAnswers(mapToAnswer(command.answers(), problem));
		problem.setProblemImage(command.imageSource());
		problem.setSolutionVideoLink(command.solutionVideoLink());
		problem.setSolutionImage(command.solutionImage());
		problem.setYears(command.year());
		problem.setSchoolCode(command.schoolCode());
		problem.setMemo(command.memo());
		problem.setLocation(schoolLocation);

		problem.setUpdatedAt(LocalDateTime.now());

		publishUpdateMessage(problem);

		log.info("[ProblemUpdateManager.update] problem updated - id: {}, memberId: {}, course: {}",
			command.problemId(), command.requestMemberId(), command.coursePath());
	}

	private void publishUpdateMessage(final Problem problem) {
		outboxPublisher.publish("problem-info-updated", new ProblemUpdatedEventPayload(
			problem.getId(),
			problem.getCoursePath(),
			problem.getProblemImage(),
			problem.getType(),
			problem.getDifficulty(),
			problem.getUpdatedAt(),
			problem.getYears(),
			problem.getSchoolCode(),
			problem.getLocation(),
			problem.getMemo()
		));
	}

	private void isOwner(final Long requestMemberId, final Problem problem) {
		if (!requestMemberId.equals(problem.getMemberId())) {
			log.warn("[ProblemUpdateManager.isOwner] is not owner - problemId: {}, ownerId: {}, requestMemberId: {}",
				problem.getId(), problem.getMemberId(), requestMemberId);
			throw new CannotAccessProblemException();
		}
	}

	private Set<Answer> mapToAnswer(final Set<String> answers, final Problem problem) {
		return answers.stream()
			.map(String::trim)
			.map(answer -> Answer.of(snowflake.nextId(), answer, problem))
			.collect(Collectors.toSet());
	}

	private record ProblemUpdatedEventPayload(
		Long problemId,
		String coursePath,
		String problemImage,
		AnswerType answerType,
		Difficulty difficulty,
		LocalDateTime updatedAt,
		Integer year,
		String schoolCode,
		String location,
		String memo
	) implements EventPayload {
	}
}
