package kr.co.mathrank.domain.problem.single.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemUpdateCommand;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.exception.CannotFindSingleProblemException;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemUpdateService {
	private final SingleProblemRepository singleProblemRepository;
	private final TransactionalOutboxPublisher outboxPublisher;

	@Transactional
	public void update(@NotNull @Valid final SingleProblemUpdateCommand command) {
		final SingleProblem singleProblem = findSingleProblem(command.singleProblemId());
		singleProblem.setSingleProblemName(command.singleProblemName());
		outboxPublisher.publish("single-problem-name-updated", SingleProblemUpdatedEventPayload.from(singleProblem));
	}

	private SingleProblem findSingleProblem(final Long singleProblemId) {
		return singleProblemRepository.findById(singleProblemId)
			.orElseThrow(() -> {
				log.info("[SingleProblemUpdateService.update] cannot find single problem - singleProblemId: {}",
					singleProblemId);
				return new CannotFindSingleProblemException();
			});
	}

	record SingleProblemUpdatedEventPayload(
		Long singleProblemId,
		String singleProblemName,
		Long problemId,
		Long memberId
	) implements EventPayload {
		public static SingleProblemUpdatedEventPayload from(final SingleProblem singleProblem) {
			return new SingleProblemUpdatedEventPayload(
				singleProblem.getId(),
				singleProblem.getSingleProblemName(),
				singleProblem.getProblemId(),
				singleProblem.getMemberId()
			);
		}
	}
}
