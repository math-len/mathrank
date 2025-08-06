package kr.co.mathrank.domain.problem.single.read.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(indexes = {
	@Index(name = "idx_coursePath", columnList = "course_path"),
	@Index(name = "idx_answerType", columnList = "answer_type"),
	@Index(name = "idx_difficulty", columnList = "difficulty"),
	@Index(name = "idx_firstTrySuccessCount", columnList = "first_try_success_count desc"),
	@Index(name = "idx_attemptedUserDistinctCount", columnList = "attempted_user_distinct_count desc"),
	@Index(name = "idx_accuracy", columnList = "accuracy desc"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
public class SingleProblemReadModel implements Persistable<Long> {
	@Id
	@Setter(AccessLevel.PRIVATE)
	private Long id; // single problem id

	@Column(unique = true, nullable = false)
	@Setter(AccessLevel.PRIVATE)
	private Long problemId; // problem id

	private String problemImage;

	private String coursePath;

	@Enumerated(EnumType.STRING)
	private AnswerType answerType;

	@Convert(converter = DifficultyConverter.class)
	private Difficulty difficulty;

	private Long firstTrySuccessCount = 0L; // 첫번째 시도에서 성공한 횟수

	private Long totalAttemptedCount = 0L; // 문제를 풀려고 시도한 총 횟수

	private Long attemptedUserDistinctCount = 0L; // 해당 문제를 풀려고 한 사용자 수

	@Setter(AccessLevel.NONE)
	private Double accuracy; // 정답률 계산: (firstTrySuccessCount / attemptedUserDistinctCount) * 100

	@Transient
	@Setter(AccessLevel.NONE)
	private boolean isNew = false;

	public static SingleProblemReadModel of(
		final Long singleProblemId,
		final Long problemId,
		final String problemImage,
		final String coursePath,
		final AnswerType answerType,
		final Difficulty difficulty,
		final Long firstTrySuccessCount,
		final Long totalAttemptedCount,
		final Long attemptedUserDistinctCount
	) {
		final SingleProblemReadModel model = new SingleProblemReadModel();
		model.setId(singleProblemId);
		model.setProblemImage(problemImage);
		model.setProblemId(problemId);
		model.setCoursePath(coursePath);
		model.setAnswerType(answerType);
		model.setDifficulty(difficulty);
		model.setFirstTrySuccessCount(firstTrySuccessCount);
		model.setTotalAttemptedCount(totalAttemptedCount);
		model.setAttemptedUserDistinctCount(attemptedUserDistinctCount);
		model.isNew = true;

		return model;
	}

	@PrePersist
	@PreUpdate
	void updateAccuracy() {
		if (attemptedUserDistinctCount == 0) {
			this.accuracy = 0.00d;
			return;
		}

		double raw = (firstTrySuccessCount.doubleValue() / attemptedUserDistinctCount.doubleValue()) * 100;

		// 소수점 둘째 자리까지 반올림
		this.accuracy = BigDecimal.valueOf(raw)
			.setScale(2, RoundingMode.HALF_UP)
			.doubleValue();
	}

	@Override
	public boolean isNew() {
		return isNew;
	}
}
