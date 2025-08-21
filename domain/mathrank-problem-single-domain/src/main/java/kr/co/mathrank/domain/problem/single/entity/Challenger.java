package kr.co.mathrank.domain.problem.single.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(
		name = "idx_memberId_singleProblemId",
		columnNames = {"member_id", "single_problem_id"}
	)
)
@Getter
public class Challenger {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	@OneToMany(mappedBy = "challenger", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private List<ChallengeLog> challengeLogs = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	private SingleProblem singleProblem;

	private boolean successAtFirstTry;

	public static Challenger of(final Long memberId, final SingleProblem singleProblem) {
		final Challenger challenger = new Challenger();
		challenger.memberId = memberId;
		challenger.singleProblem = singleProblem;

		return challenger;
	}

	public ChallengeLog addChallengeLog(final Boolean success, final List<String> submittedAnswer, final List<String> correctAnswer) {
		final ChallengeLog challengeLog = ChallengeLog.of(this, success, submittedAnswer, correctAnswer);

		// 첫 제출
		if (challengeLogs.isEmpty()) {
			successAtFirstTry = success;
		}
		challengeLogs.add(challengeLog);

		return challengeLog;
	}
}
