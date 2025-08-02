package kr.co.mathrank.domain.problem.single.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeLog implements Persistable<Long> {
	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private SingleProblem singleProblem;

	private Long memberId;

	private Boolean success;

	private String submittedAnswer;

	private String correctAnswer;

	private LocalDateTime challengeAt;

	@CreationTimestamp
	private LocalDateTime createdAt;

	public static ChallengeLog of(final Long id, final SingleProblem singleProblem, final Long memberId,
		final Boolean success, final String submittedAnswer, final String correctAnswer,
		final LocalDateTime challengeAt) {
		final ChallengeLog challengeLog = new ChallengeLog();
		challengeLog.id = id;
		challengeLog.singleProblem = singleProblem;
		challengeLog.memberId = memberId;
		challengeLog.success = success;
		challengeLog.submittedAnswer = submittedAnswer;
		challengeLog.correctAnswer = correctAnswer;
		challengeLog.challengeAt = challengeAt;

		return challengeLog;
	}

	@Override
	public boolean isNew() {
		return createdAt == null;
	}
}
