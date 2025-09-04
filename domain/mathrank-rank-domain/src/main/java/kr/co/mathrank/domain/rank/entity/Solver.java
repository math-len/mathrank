package kr.co.mathrank.domain.rank.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(
	indexes = {
		@Index(name = "idx_score_memberId", columnList = "score desc, member_id")
	}
)
@Getter
public class Solver {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "solver", cascade = CascadeType.PERSIST)
	private final List<SolveLog> solveLogs = new ArrayList<>();

	private Long score = 0L;

	@Column(unique = true)
	private Long memberId;

	public static Solver of(final Long memberId) {
		final Solver solver = new Solver();
		solver.memberId = memberId;

		return solver;
	}

	public SolveLog addSolveLog(final Long problemId, final Long singleProblemId, final boolean success, final Integer score) {
		final SolveLog solveLog = SolveLog.of(problemId, singleProblemId, this, success);
		solveLogs.add(solveLog);
		if (success) {
			this.score += score;
		}

		return solveLog;
	}
}
