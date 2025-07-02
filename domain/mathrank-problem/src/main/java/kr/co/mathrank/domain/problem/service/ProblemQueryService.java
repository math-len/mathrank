package kr.co.mathrank.domain.problem.service;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.dto.ProblemQueryCommand;
import kr.co.mathrank.domain.problem.dto.ProblemQueryPageResult;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class ProblemQueryService {
	private final ProblemRepository problemRepository;

	@Transactional
	public ProblemQueryPageResult query(@NotNull @Valid final ProblemQueryCommand command) {
		final List<Problem> problems = problemRepository.query(command.memberId(),
			command.difficulty(),
			command.answerType(),
			command.problemCourse(),
			command.pageSize(),
			command.pageNumber());
		final Long totalCount = problemRepository.count(command.memberId(),
			command.difficulty(),
			command.answerType(),
			command.problemCourse());

		return ProblemQueryPageResult.of(
			problems,
			command.pageNumber(),
			command.pageSize(),
			PageUtil.getNextPages(command.pageSize(), command.pageNumber(), totalCount, problems.size())
		);
	}
}
