package kr.co.mathrank.domain.problem.single.entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(
		name = "unique_memberId_singleProblemId",
		columnNames = {"member_id", "single_problem_id"}
	),
	indexes = @Index(
		name = "idx_memberId_singleProblemId_successAtFirstTry",
		columnList = "member_id, single_problem_id, success_at_first_try"
	)
)
@Getter
public class Challenger {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	@OneToMany(mappedBy = "challenger", orphanRemoval = true, cascade = CascadeType.PERSIST)
	@OrderBy("challengedAt")
	@BatchSize(size = 100)
	private final List<ChallengeLog> challengeLogs = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	private SingleProblem singleProblem;

	private boolean successAtFirstTry = false;

	public static Challenger of(final Long memberId, final SingleProblem singleProblem) {
		final Challenger challenger = new Challenger();
		challenger.memberId = memberId;
		challenger.singleProblem = singleProblem;

		return challenger;
	}

	public ChallengeLog addChallengeLog(final boolean success, final List<String> submittedAnswer, final List<String> correctAnswer, final
		Duration elapsedTime) {
		// 첫 제출
		boolean isFirstLog = challengeLogs.isEmpty();
		if (isFirstLog) {
			successAtFirstTry = success;
		}

		final ChallengeLog challengeLog = ChallengeLog.of(this, success, submittedAnswer, correctAnswer, elapsedTime, isFirstLog);

		challengeLogs.add(challengeLog);

		return challengeLog;
	}
}
