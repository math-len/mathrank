package kr.co.mathrank.domain.rank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(indexes =
	@Index(name = "idx_unique_solver_singleProblemId", columnList = "solver_id, single_problem_id", unique = true)
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SolveLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long singleProblemId;

	private Long problemId;

	@ManyToOne(fetch = FetchType.LAZY)
	private Solver solver;

	private boolean success;

	public static SolveLog of(final Long singleProblemId, final Long problemId, final Solver solver, final boolean success) {
		final SolveLog solverLog = new SolveLog();

		solverLog.singleProblemId = singleProblemId;
		solverLog.problemId = problemId;
		solverLog.solver = solver;
		solverLog.success = success;

		return solverLog;
	}
}
