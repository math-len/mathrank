package kr.co.mathrank.domain.rank.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import kr.co.mathrank.domain.rank.repository.SolverRepository;

@DataJpaTest
class SolveLogTest {
	@Autowired
	private SolverRepository solverRepository;

	@Test
	void 사용자ID와_개별문제ID_쌍은_중복될_수_없다() {
		final Solver solver = new Solver();

		solver.addSolveLog(1L, 2L, true, 100);
		solver.addSolveLog(1L, 2L, true, 100);

		Assertions.assertThrows(DataIntegrityViolationException.class, () -> solverRepository.save(solver));
	}

	@Test
	void 문제ID는_중복_가능하다() {
		final Solver solver = new Solver();

		solver.addSolveLog(1L, 1L, true, 100);
		solver.addSolveLog(1L, 2L, true, 100);

		Assertions.assertDoesNotThrow(() -> solverRepository.save(solver));
	}

	@Test
	void 풀이에_성공한거면_점수추가한다() {
		final Solver solver = new Solver();

		solver.addSolveLog(1L, 2L, true, 100);
		solver.addSolveLog(1L, 3L, false, 100);

		Assertions.assertEquals(100, solver.getScore());
	}
}
