package kr.co.mathrank.domain.rank.service;

import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankScoreQueryManager {
	private final SolverRepository solverRepository;

	public Long getScore(final Long memberId) {
		return solverRepository.findScoreByMemberId(memberId);
	}
}
