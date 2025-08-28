package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(
	indexes = @Index(name = "idx_memberId_assessmentId", columnList = "member_id, assessment_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssessmentSubmission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Assessment assessment;

	private Long memberId;

	@Enumerated(EnumType.STRING)
	private EvaluationStatus evaluationStatus = EvaluationStatus.PENDING; // 초기 상태는 항상 지연 상태

	private Integer totalScore;

	@OneToMany(mappedBy = "submission", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private final List<AssessmentItemSubmission> submittedItemAnswers = new ArrayList<>();

	@CreationTimestamp
	private LocalDateTime submittedAt;

	@Convert(converter = AssessmentDurationConverter.class)
	private Duration elapsedTime;

	private Boolean isFirstSubmission;

	static AssessmentSubmission of(final Assessment assessment, final Long memberId, final Duration elapsedTime, final boolean isFirstSubmission) {
		final AssessmentSubmission assessmentSubmission = new AssessmentSubmission();
		assessmentSubmission.assessment = assessment;
		assessmentSubmission.memberId = memberId;
		assessmentSubmission.elapsedTime = elapsedTime;
		assessmentSubmission.isFirstSubmission = isFirstSubmission;

		return assessmentSubmission;
	}

	public int grade(final List<GradeResult> gradeResults) {
		if (evaluationStatus == EvaluationStatus.FINISHED) {
			log.info("[AssessmentSubmission.grade] already graded - submissionId: {}", id);
			throw new IllegalStateException("이미 채점된 시험지입니다.");
		}

		if (gradeResults.size() != submittedItemAnswers.size()) {
			log.warn(
				"[AssessmentSubmission.grade] gradeResults size is not matched with current item submissions size - submissionId: {}, submissions size: {}, gradeResults size: {}",
				id, submittedItemAnswers.size(), gradeResults.size());
			throw new IllegalArgumentException("채점 결과 갯수와 제출 답안의 갯수가 일치하지 않습니다.");
		}

		int totalScore = 0;
		for (int i = 0; i < gradeResults.size(); i++) {
			final AssessmentItemSubmission itemSubmission = submittedItemAnswers.get(i);
			final GradeResult gradeResult = gradeResults.get(i);

			totalScore += itemSubmission.grade(gradeResult);
		}

		this.totalScore = totalScore;
		this.evaluationStatus = EvaluationStatus.FINISHED;
		return totalScore;
	}

	void addItemSubmission(final AssessmentItem assessmentItem, final List<String> submittedAnswer) {
		final AssessmentItemSubmission assessmentItemSubmission = AssessmentItemSubmission.of(this, assessmentItem, submittedAnswer);
		this.submittedItemAnswers.add(assessmentItemSubmission);
	}

	/**
	 * 제출된 모든 문항 답안을 반환합니다.
	 *
	 * <p>반환되는 리스트는 {@link java.util.Collections#unmodifiableList(List)}로
	 * 감싸져 있어 외부에서 수정할 수 없습니다.
	 * 리스트의 요소를 변경하려 하면 {@link UnsupportedOperationException}이 발생합니다.</p>
	 *
	 * @return 제출된 문항 답안들의 불변 리스트
	 */
	public List<AssessmentItemSubmission> getSubmittedItemAnswers() {
		return Collections.unmodifiableList(submittedItemAnswers);
	}

	@PreUpdate
	@PrePersist
	private void updateEvaluateStatus() {
		final boolean allEvaluated = this.getSubmittedItemAnswers().stream()
			.map(AssessmentItemSubmission::getCorrect)
			.allMatch(Objects::nonNull);
		if (allEvaluated) {
			this.evaluationStatus = EvaluationStatus.FINISHED;
		}
	}
}
