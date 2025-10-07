package kr.co.mathrank.domain.problem.single.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemDeleteCommand;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.exception.CannotFindSingleProblemException;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemDeleteService {
	private final SingleProblemRepository singleProblemRepository;
	private final TransactionalOutboxPublisher outboxPublisher;

	@Transactional
	public void delete(@NotNull @Valid final SingleProblemDeleteCommand command) {
		final SingleProblem singleProblem = getSingleProblem(command);
		singleProblemRepository.delete(singleProblem);
		outboxPublisher.publish("single-problem-deleted", SingleProblemDeletedEvent.of(singleProblem));
		log.info("[SingleProblemDeleteService.delete] single problem delete successful - singleProblemId: {}", singleProblem.getId());
	}

	@Transactional
	public void deleteByProblemId(@NotNull final Long problemId) {
		final SingleProblem singleProblem = getByProblemId(problemId);
		singleProblemRepository.delete(singleProblem);
		outboxPublisher.publish("single-problem-deleted", SingleProblemDeletedEvent.of(singleProblem));
		log.info("[SingleProblemDeleteService.delete] single problem delete successful - singleProblemId: {}, problemId: {}", singleProblem.getId(), problemId);
	}

	public SingleProblem getByProblemId(@NotNull final Long problemId) {
		return singleProblemRepository.findByProblemId(problemId)
			.orElseThrow(() -> {
				log.info("[SingleProblemDeleteService.getSingleProblem] cannot found singleProblem - problemId: {}",
					problemId);
				return new CannotFindSingleProblemException();
			});
	}

	private SingleProblem getSingleProblem(SingleProblemDeleteCommand command) {
		return singleProblemRepository.findById(command.singleProblemId())
			.orElseThrow(() -> {
				log.info("[SingleProblemDeleteService.getSingleProblem] cannot found singleProblem - singleProblemId: {}",
					command.singleProblemId());
				return new CannotFindSingleProblemException();
			});
	}

	record SingleProblemDeletedEvent(
		Long singleProblemId,
		String singleProblemName,
		Long problemId,
		Long memberId
	) implements EventPayload {
		public static SingleProblemDeletedEvent of(
			final SingleProblem deletedProblem
		) {
			return new SingleProblemDeletedEvent(
				deletedProblem.getId(),
				deletedProblem.getSingleProblemName(),
				deletedProblem.getProblemId(),
				deletedProblem.getMemberId()
			);
		}
	}
}
