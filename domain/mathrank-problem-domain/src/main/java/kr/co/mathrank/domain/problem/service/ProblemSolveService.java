package kr.co.mathrank.domain.problem.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.dto.ProblemSolveCommand;
import kr.co.mathrank.domain.problem.dto.ProblemSolveResult;
import kr.co.mathrank.domain.problem.exception.CannotFoundProblemException;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProblemSolveService {
	private final ProblemRepository problemRepository;

	public ProblemSolveResult solve(@NotNull @Valid final ProblemSolveCommand command) {
		final ProblemQueryResult queryResult = problemRepository.findProblemByIdWithAnswer(command.problemId())
			.map(ProblemQueryResult::from)
			.orElseThrow(() -> {
				log.warn("[ProblemSolveService.solve] cannot find - problemId: {}", command.problemId());
				return new CannotFoundProblemException(command.problemId());
			});

		final boolean success = match(command.submittedAnswer(), queryResult.answer());

		log.info("[ProblemSolveService.solve] solved problem - problemId: {}, submittedAnswer: {}, correctAnswer: {}, success: {}", command.problemId(), command.submittedAnswer(), queryResult.answer(), success);
		return new ProblemSolveResult(success, command.submittedAnswer(), queryResult.answer());
	}

	private boolean match(final List<String> submittedAnswer, final Set<String> correctAnswer) {
		// 제출된 정답이 정답을 모두 포함하는지
		for (final String currCorrectAnswer : correctAnswer) {
			if (!submittedAnswer.contains(currCorrectAnswer)) {
				// 제출된 정답에 실제 정답이 포함되지 않았음
				return false;
			}
		}

		// 추가적인 정답을 제출했음
		return submittedAnswer.size() == correctAnswer.size();
	}
}
