package kr.co.mathrank.domain.rank.service;

import org.springframework.stereotype.Service;

import kr.co.mathrank.domain.rank.dto.RankQueryResult;
import kr.co.mathrank.domain.rank.entity.Tier;
import kr.co.mathrank.domain.rank.repository.RankRepository;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankQueryService {
	private final RankRepository rankRepository;

	public RankQueryResult getRank(final Long memberId) {
		final long rank = rankRepository.getRank(String.valueOf(memberId));
		final long totalMemberCount = rankRepository.getTotalRankerCount();
		final Tier tier = Tier.getMatchTier(rank, totalMemberCount);
		final Long score = rankRepository.getScore(String.valueOf(memberId));

		return RankQueryResult.of(rank, tier, score, totalMemberCount);
	}
}
