package kr.co.mathrank.domain.problem.service;

import java.util.List;

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
			command.difficultyMinInclude(),
			command.difficultyMaxInclude(),
			command.answerType(),
			command.coursePath(),
			command.pageSize(),
			command.pageNumber(),
			command.videoExist(),
			command.year());
		final Long totalCount = problemRepository.count(command.memberId(),
			command.difficultyMinInclude(),
			command.difficultyMaxInclude(),
			command.coursePath(),
			command.answerType(),
			command.videoExist(),
			command.year());

		return ProblemQueryPageResult.of(
			problems,
			command.pageNumber(),
			command.pageSize(),
			PageUtil.getNextPages(command.pageSize(), command.pageNumber(), totalCount, problems.size())
		);
	}
}
