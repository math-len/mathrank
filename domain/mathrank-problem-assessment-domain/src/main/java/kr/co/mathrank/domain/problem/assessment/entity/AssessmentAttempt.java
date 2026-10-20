package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

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
	/** 시간 제한이 없는 응시는 시작 후 이 시간이 지나면 임시 저장 답안으로 자동 제출된다. */
	public static final Duration AUTO_SUBMIT_AFTER = Duration.ofHours(3);

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

	@Column(name = "expires_at")
	private Instant expiresAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AssessmentAttemptStatus status;

	@Column(name = "submission_id")
	private Long submissionId;

	@Column(name = "active_key")
	private String activeKey;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "draft_answers")
	private List<List<String>> draftAnswers;

	@Column(name = "draft_saved_at")
	private Instant draftSavedAt;

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
		return expiresAt != null && now.isAfter(expiresAt);
	}

	public boolean isAnswerInputEnabledAt(final Instant now) {
		return !now.isBefore(answerUnlockedAt) && !isExpiredAt(now);
	}

	public Instant getAutoSubmitAt() {
		return expiresAt == null ? startedAt.plus(AUTO_SUBMIT_AFTER) : null;
	}

	/** 시간 제한이 없는 진행 중 응시가 자동 제출 시각을 지났는지 판단한다. */
	public boolean isAutoSubmitDueAt(final Instant now) {
		return status == AssessmentAttemptStatus.ACTIVE
			&& expiresAt == null
			&& !now.isBefore(getAutoSubmitAt());
	}

	public void saveDraft(final List<List<String>> answers, final Instant now) {
		draftAnswers = answers.stream().map(List::copyOf).toList();
		draftSavedAt = now;
	}

	/** 자동 제출용 답안. 저장된 답안이 없거나 문항 수가 다르면 빈 답안으로 맞춘다. */
	public List<List<String>> draftAnswersFor(final int itemCount) {
		final List<List<String>> answers = new ArrayList<>(itemCount);
		for (int index = 0; index < itemCount; index++) {
			final boolean hasDraft = draftAnswers != null
				&& index < draftAnswers.size()
				&& draftAnswers.get(index) != null;
			answers.add(hasDraft ? draftAnswers.get(index) : List.of());
		}
		return answers;
	}

	public void removeTimeLimits() {
		answerUnlockedAt = startedAt;
		expiresAt = null;
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
