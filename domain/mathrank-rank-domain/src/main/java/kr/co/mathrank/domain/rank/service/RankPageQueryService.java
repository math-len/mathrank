package kr.co.mathrank.domain.rank.service;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.rank.dto.RankItemResult;
import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.entity.Tier;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class RankPageQueryService {
	private final SolverRepository solverRepository;

	@Transactional(readOnly = true)
	public PageResult<RankItemResult> queryResultPageResult(
		@NotNull @Range(min = 1, max = 20) final Integer pageSize,
		@NotNull @Range(min = 1, max = 2000) final Integer pageNumber
	) {
		final int currentPageNumber = pageNumber - 1;

		final Sort sort = Sort.by(Sort.Direction.DESC, "score");
		final PageRequest pageRequest = PageRequest.of(currentPageNumber, pageSize, sort);
		final List<Solver> solvers = solverRepository.findAllSolversDescendingScores(pageRequest);
		final Long totalCount = solverRepository.count();

		final PageResult<Solver> solverPageResult = PageResult.of(solvers,
			currentPageNumber,
			pageSize,
			PageUtil.getNextPages(
				pageSize,
				currentPageNumber,
				totalCount,
				solvers.size()
			)
		);

		return solverPageResult
			.map(solver -> this.mapToRankQueryResult(solver, totalCount));
	}

	private RankItemResult mapToRankQueryResult(final Solver solver, final Long totalCount) {
		final Long rank = solverRepository.findRankByMemberId(solver.getMemberId()) + 1;

		return RankItemResult.from(
			solver,
			rank,
			Tier.getMatchTier(rank, totalCount)
		);
	}
}
