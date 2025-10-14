package kr.co.mathrank.domain.rank.service;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.rank.RankDomainConfiguration;
import kr.co.mathrank.domain.rank.dto.SchoolRankQueryResult;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
@CacheConfig(cacheNames = RankDomainConfiguration.SCHOOL_RANK_BOARD_CACHE_NAME)
public class SchoolRankQueryService {
	private final SolverRepository solverRepository;

	@Cacheable(
		key = "'pageNumber::' + #pageNumber + '::pageSize::' + #pageSize",
		condition = "#pageNumber <= 4"
	)
	public PageResult<SchoolRankQueryResult> querySchoolRanks(
		@NotNull final Integer pageSize,
		@NotNull final Integer pageNumber
	) {
		final List<SchoolRankQueryResult> schoolRankQueryResults = solverRepository.findSchoolScores(
			PageRequest.of(pageNumber - 1, pageSize));
		final Long countSchoolCode = solverRepository.countDistinctSchools();

		return PageResult.of(
			schoolRankQueryResults,
			pageNumber,
			pageSize,
			PageUtil.getNextPages(
				pageSize,
				pageNumber,
				countSchoolCode,
				schoolRankQueryResults.size()
			));
	}
}
