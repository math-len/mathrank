package kr.co.mathrank.domain.problem.single.read.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(
		name = "unique_singleProblemSolver_memberId_singleProblemId",
		columnNames = {"member_id", "single_problem_read_model_id"}
	)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "singleProblemReadModel")
public class SingleProblemSolver {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private boolean success; // success or fail 둘 중 하나

	@ManyToOne(fetch = FetchType.LAZY)
	private SingleProblemReadModel singleProblemReadModel;

	public static SingleProblemSolver of(final SingleProblemReadModel singleProblemReadModel, final Long memberId, final boolean success) {
		final SingleProblemSolver singleProblemSolver = new SingleProblemSolver();
		singleProblemSolver.memberId = memberId;
		singleProblemSolver.singleProblemReadModel =singleProblemReadModel;
		singleProblemSolver.success = success;

		return singleProblemSolver;
	}
}
