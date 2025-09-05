package kr.co.mathrank.domain.problem.service;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.problem.dto.ProblemQuery;
import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.exception.CannotFoundProblemException;
import kr.co.mathrank.domain.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProblemQueryService {
	private final ProblemRepository problemRepository;

	public ProblemQueryResult getSingle(@NotNull final Long problemId) {
		return problemRepository.findById(problemId)
			.map(ProblemQueryResult::from)
			.orElseThrow(() -> {
				log.warn("[ProblemQueryService.getSingle] cannot find - problemId: {}", problemId);
				return new CannotFoundProblemException(problemId);
			});
	}

	@Transactional
	public PageResult<ProblemQueryResult> query(
		@NotNull @Valid final ProblemQuery query,
		@NotNull
		@Range(min = 1, max = 20)
		Integer pageSize,
		@NotNull
		@Range(min = 1, max = 1000)
		Integer pageNumber) {

		final List<Problem> problems = problemRepository.query(query, pageSize, pageNumber);
		final Long totalCount = problemRepository.count(query);

		return PageResult.of(
			problems.stream()
				.map(ProblemQueryResult::from)
				.toList(),
			pageNumber,
			pageSize,
			PageUtil.getNextPages(pageSize, pageNumber, totalCount, problems.size())
		);
	}
}
