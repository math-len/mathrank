package kr.co.mathrank.domain.rank.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.repository.SolverRepository;

@SpringBootTest
class RankQueryServiceTest {
	@Autowired
	private RankQueryService rankQueryService;
	@Autowired
	private SolverRepository solverRepository;

	@Test
	@Transactional
	void 순위가_출력된다() {
		final Solver solver1 = Solver.of(1L);
		final Solver solver2 = Solver.of(2L);

		// solver1 : 점수 50점
		solver1.addSolveLog(1L, 2L, true, 50);
		// solver2 : 점수 40점
		solver2.addSolveLog(1L, 2L, true, 40);

		solverRepository.save(solver1);
		solverRepository.save(solver2);

		Assertions.assertAll(
			// solver1 는 랭크 1위
			() -> Assertions.assertEquals(1, rankQueryService.getRank(solver1.getId()).rank()),
			// solver2 는 랭크 2위
			() -> Assertions.assertEquals(2, rankQueryService.getRank(solver2.getId()).rank())
		);
	}

	@Test
	@Transactional
	void 순위는_자신보다_높은_점수를_가진_사람들의_수로_결정된다() {
		final Solver solver1 = Solver.of(1L);
		final Solver solver2 = Solver.of(2L);
		final Solver solver3 = Solver.of(3L);
		final Solver solver4 = Solver.of(4L);

		// solver1 : 점수 50점
		solver1.addSolveLog(1L, 2L, true, 50);
		solver2.addSolveLog(1L, 2L, true, 50);
		solver3.addSolveLog(1L, 2L, true, 50);
		// solver4 : 점수 40점
		solver4.addSolveLog(1L, 2L, true, 40);

		solverRepository.save(solver1);
		solverRepository.save(solver2);
		solverRepository.save(solver3);
		solverRepository.save(solver4);

		// solver4 는 랭크 4위
		// solver 보다 높은 점수가 3명임
		Assertions.assertEquals(4, rankQueryService.getRank(solver4.getId()).rank());
	}

	@Test
	@Transactional
	void 동점인_사람들은_같은_랭크로_결정된다() {
		final Solver solver1 = Solver.of(1L);
		final Solver solver2 = Solver.of(2L);
		final Solver solver3 = Solver.of(3L);
		final Solver solver4 = Solver.of(4L);
		final Solver solver5 = Solver.of(5L);

		// solver1 : 점수 50점
		solver1.addSolveLog(1L, 2L, true, 50);
		solver2.addSolveLog(1L, 2L, true, 50);
		solver3.addSolveLog(1L, 2L, true, 50);
		// solver4 : 점수 40점
		solver4.addSolveLog(1L, 2L, true, 40);
		solver5.addSolveLog(1L, 2L, true, 40);

		solverRepository.save(solver1);
		solverRepository.save(solver2);
		solverRepository.save(solver3);
		solverRepository.save(solver4);
		solverRepository.save(solver5);

		// 동일 점수는 동일 랭크
		Assertions.assertAll(
			() -> Assertions.assertEquals(1, rankQueryService.getRank(solver1.getId()).rank()),
			() -> Assertions.assertEquals(1, rankQueryService.getRank(solver2.getId()).rank()),
			() -> Assertions.assertEquals(1, rankQueryService.getRank(solver3.getId()).rank()),

			() -> Assertions.assertEquals(4, rankQueryService.getRank(solver4.getId()).rank()),
			() -> Assertions.assertEquals(4, rankQueryService.getRank(solver5.getId()).rank())
		);
	}
}
