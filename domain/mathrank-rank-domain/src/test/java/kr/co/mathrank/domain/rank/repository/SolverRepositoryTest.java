package kr.co.mathrank.domain.rank.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import kr.co.mathrank.domain.rank.entity.Solver;

@DataJpaTest
class SolverRepositoryTest {
	@Autowired
	private SolverRepository solverRepository;

	@Test
	void 동점학교는_같은_등수() {
		final String schoolCode1 = "first";
		final String schoolCode2 = "second";

		final Solver solver1 = Solver.of(1L, schoolCode1);
		final Solver solver2 = Solver.of(2L, schoolCode2);

		solver1.addSolveLog(1L, 2L, true, 30);
		solver2.addSolveLog(1L, 2L, true, 30);

		solverRepository.saveAll(List.of(solver1, solver2));

		Assertions.assertAll(
			() -> Assertions.assertEquals(1, solverRepository.findSchoolScores(Pageable.ofSize(10)).get(0).rank()),
			() -> Assertions.assertEquals(1, solverRepository.findSchoolScores(Pageable.ofSize(10)).get(1).rank())
		);
	}

	@Test
	void 동점_학교들_갯수만큼_랭크에_반영() {
		final String schoolCode1 = "first";
		final String schoolCode2 = "second";
		final String schoolCode3 = "third";

		final Solver solver1 = Solver.of(1L, schoolCode1);
		final Solver solver2 = Solver.of(2L, schoolCode2);
		final Solver solver3 = Solver.of(3L, schoolCode3);

		solver1.addSolveLog(1L, 2L, true, 30);
		solver2.addSolveLog(1L, 2L, true, 30);
		solver3.addSolveLog(1L, 2L, true, 10); // 너가 꼴등

		solverRepository.saveAll(List.of(solver1, solver2, solver3));

		Assertions.assertAll(
			() -> Assertions.assertEquals(1, solverRepository.findSchoolScores(Pageable.ofSize(10)).get(0).rank()),
			() -> Assertions.assertEquals(1, solverRepository.findSchoolScores(Pageable.ofSize(10)).get(1).rank()),
			() -> Assertions.assertEquals(3, solverRepository.findSchoolScores(Pageable.ofSize(10)).get(2).rank())
		);
	}

	@Test
	void 독립적인_학교_갯수_카운트() {
		final String schoolCode1 = "first";
		final String schoolCode2 = "second";

		final Solver solver1 = Solver.of(1L, schoolCode1);
		final Solver solver2 = Solver.of(2L, schoolCode1);
		// 얘만 다른 학교
		final Solver solver3 = Solver.of(3L, schoolCode2);

		solver1.addSolveLog(1L, 2L, true, 30);
		solver2.addSolveLog(1L, 2L, true, 30);
		solver3.addSolveLog(1L, 2L, true, 30);

		solverRepository.saveAll(List.of(solver1, solver2, solver3));

		Assertions.assertEquals(2, solverRepository.countDistinctSchools());
	}
}