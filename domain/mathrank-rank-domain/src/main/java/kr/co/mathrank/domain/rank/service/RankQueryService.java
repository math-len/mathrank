package kr.co.mathrank.domain.rank.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import kr.co.mathrank.domain.rank.RankDomainConfiguration;
import kr.co.mathrank.domain.rank.dto.RankQueryResult;
import kr.co.mathrank.domain.rank.entity.Tier;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = RankDomainConfiguration.USER_RANK_CACHE_NAME)
public class RankQueryService {
	private final SolverRepository solverRepository;

	@Cacheable(key = "#memberId")
	public RankQueryResult getRank(final Long memberId) {
		final Long rank = solverRepository.findRankByMemberId(memberId);
		final Long score = solverRepository.findScoreByMemberId(memberId);
		final long totalMemberCount = solverRepository.count();

		final Tier tier = Tier.getMatchTier(rank, totalMemberCount);

		return RankQueryResult.of(rank, tier, score, totalMemberCount);
	}
}
