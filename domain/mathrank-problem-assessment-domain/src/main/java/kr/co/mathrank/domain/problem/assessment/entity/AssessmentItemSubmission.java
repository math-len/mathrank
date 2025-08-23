package kr.co.mathrank.domain.problem.assessment.entity;

import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "submission")
public class AssessmentItemSubmission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private AssessmentSubmission submission;

	/** 이 답안이 속한 원본 평가 문항 */
	@ManyToOne(fetch = FetchType.LAZY)
	private AssessmentItem assessmentItem;

	/**
	 * 사용자가 제출한 답안.
	 * <p>
	 * 주관식 단답형 등 정답이 여러 개일 수 있는 경우를 대비해 List로 관리합니다.
	 * DB에는 JSON 형태로 저장됩니다.
	 * </p>
	 */
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> submittedAnswer;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> realAnswer; // 실제 정답 기록

	private Boolean correct;

	static AssessmentItemSubmission of(final AssessmentSubmission submission, final AssessmentItem assessmentItem, final List<String> submittedAnswer) {
		final AssessmentItemSubmission assessmentItemSubmission = new AssessmentItemSubmission();
		assessmentItemSubmission.submission = submission;
		assessmentItemSubmission.assessmentItem = assessmentItem;
		assessmentItemSubmission.submittedAnswer = submittedAnswer;

		return assessmentItemSubmission;
	}

	int grade(final GradeResult gradeResult) {
		if (!this.assessmentItem.getProblemId().equals(gradeResult.problemId())) {
			log.error(
				"[AssessmentItemSubmission.grade] grade result's problemId is not matched - current submission problemId: {}, grade result problemId: {}",
				this.assessmentItem.getProblemId(), gradeResult.problemId());
			throw new IllegalArgumentException("ID가 일치하지 않습니다.");
		}
		correct = gradeResult.success();
		this.realAnswer = gradeResult.correctAnswer();

		return correct ? assessmentItem.getScore() : 0;
	}

	/**
	 * 제출된 답안 리스트를 반환합니다.
	 *
	 * <p>반환되는 리스트는 변경할 수 없는
	 * {@link java.util.Collections#unmodifiableList(List)}로 감싸져 있으므로,
	 * 호출자가 내용을 수정하려고 하면 {@link UnsupportedOperationException}이 발생합니다.</p>
	 *
	 * @return 제출된 답안의 불변 리스트
	 */
	public List<String> getSubmittedAnswer() {
		return Collections.unmodifiableList(submittedAnswer);
	}
}
