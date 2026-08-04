package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
	name = "assessment_attempt",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_assessment_attempt_number",
			columnNames = {"assessment_id", "member_id", "attempt_number"}
		),
		@UniqueConstraint(name = "uk_assessment_attempt_active_key", columnNames = "active_key")
	},
	indexes = {
		@Index(name = "idx_assessment_attempt_assessment_member", columnList = "assessment_id, member_id"),
		@Index(name = "idx_assessment_attempt_submission", columnList = "submission_id")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssessmentAttempt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "assessment_id", nullable = false)
	private Long assessmentId;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "attempt_number", nullable = false)
	private Integer attemptNumber;

	@Column(name = "started_at", nullable = false)
	private Instant startedAt;

	@Column(name = "answer_unlocked_at", nullable = false)
	private Instant answerUnlockedAt;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AssessmentAttemptStatus status;

	@Column(name = "submission_id")
	private Long submissionId;

	@Column(name = "active_key")
	private String activeKey;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public static AssessmentAttempt start(
		final Long assessmentId,
		final Long memberId,
		final Integer attemptNumber,
		final Instant startedAt,
		final Instant answerUnlockedAt,
		final Instant expiresAt
	) {
		final AssessmentAttempt attempt = new AssessmentAttempt();
		attempt.assessmentId = assessmentId;
		attempt.memberId = memberId;
		attempt.attemptNumber = attemptNumber;
		attempt.startedAt = startedAt;
		attempt.answerUnlockedAt = answerUnlockedAt;
		attempt.expiresAt = expiresAt;
		attempt.status = AssessmentAttemptStatus.ACTIVE;
		attempt.activeKey = activeKey(assessmentId, memberId);
		return attempt;
	}

	public static String activeKey(final Long assessmentId, final Long memberId) {
		return assessmentId + ":" + memberId;
	}

	public boolean isExpiredAt(final Instant now) {
		return now.isAfter(expiresAt);
	}

	public boolean isAnswerInputEnabledAt(final Instant now) {
		return !now.isBefore(answerUnlockedAt) && !isExpiredAt(now);
	}

	public void expire() {
		status = AssessmentAttemptStatus.EXPIRED;
		activeKey = null;
	}

	public void submit(final Long submissionId) {
		this.submissionId = submissionId;
		status = AssessmentAttemptStatus.SUBMITTED;
		activeKey = null;
	}
}
