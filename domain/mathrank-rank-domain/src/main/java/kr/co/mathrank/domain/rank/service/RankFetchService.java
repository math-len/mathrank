package kr.co.mathrank.domain.rank.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.repository.RankRepository;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankFetchService {
	private final RankRepository rankRepository;
	private final SolverRepository solverRepository;

	public void syncRedis() {
		final Long totalCount = solverRepository.count();

		int pageNumber = 0;
		int pageSize = 100;

		while(pageNumber * pageSize < totalCount) {
			final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
			final List<Solver> solvers = solverRepository.findAllSolversDescendingScores(pageRequest);
			solvers.forEach(solver -> rankRepository.setIfGreaterThan(String.valueOf(solver.getMemberId()), solver.getScore()));
			pageNumber++;
		}
	}
}
