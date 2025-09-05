package kr.co.mathrank.domain.rank.service;

import org.springframework.stereotype.Service;

import kr.co.mathrank.domain.rank.dto.RankQueryResult;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankQueryService {
	private final SolverRepository solverRepository;

	public RankQueryResult getRank(final Long memberId) {
		final int rank = solverRepository.findRankByMemberId(memberId) + 1;
		final long count = solverRepository.count();

		return RankQueryResult.of(rank, count);
	}
}
