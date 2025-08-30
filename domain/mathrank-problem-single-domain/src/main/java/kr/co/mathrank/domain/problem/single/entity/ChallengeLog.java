package kr.co.mathrank.domain.problem.single.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Convert;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeLog implements Persistable<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Challenger challenger;

	private Boolean success;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> submittedAnswer;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> correctAnswer;

	@Convert(converter = DurationConverter.class)
	private Duration elapsedTime;

	@CreationTimestamp
	private LocalDateTime challengedAt;

	static ChallengeLog of(final Challenger challenger, final boolean success, final List<String> submittedAnswer, final List<String> correctAnswer, final Duration elapsedTime) {
		final ChallengeLog challengeLog = new ChallengeLog();
		challengeLog.challenger = challenger;
		challengeLog.submittedAnswer = submittedAnswer;
		challengeLog.correctAnswer = correctAnswer;
		challengeLog.success = success;
		challengeLog.elapsedTime = elapsedTime;

		return challengeLog;
	}

	@Override
	public boolean isNew() {
		return challengedAt == null;
	}
}
