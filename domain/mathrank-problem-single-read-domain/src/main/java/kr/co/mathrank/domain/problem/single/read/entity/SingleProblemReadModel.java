package kr.co.mathrank.domain.problem.single.read.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
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
import lombok.ToString;

@Entity
@Getter
@Table(indexes = {
	@Index(name = "idx_coursePath", columnList = "course_path"),
	@Index(name = "idx_problemId", columnList = "problem_id"),
	@Index(name = "idx_answerType", columnList = "answer_type"),
	@Index(name = "idx_difficulty", columnList = "difficulty"),
	@Index(name = "idx_totalAttemptedCount", columnList = "total_attempted_count"),
	@Index(name = "idx_firstTrySuccessCount", columnList = "first_try_success_count desc"),
	@Index(name = "idx_attemptedUserDistinctCount", columnList = "attempted_user_distinct_count desc"),
	@Index(name = "idx_accuracy", columnList = "accuracy desc"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@ToString
public class SingleProblemReadModel implements Persistable<Long> {
	@Id
	@Setter(AccessLevel.PRIVATE)
	private Long id; // single problem id

	@Column
	@Setter(AccessLevel.PRIVATE)
	private Long problemId; // problem id

	private String singleProblemName;

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

	private LocalDateTime updatedAt;

	@Setter(AccessLevel.PRIVATE)
	private LocalDateTime createdAt; // singleProblem이 등록된 시점

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "singleProblemReadModel", cascade = CascadeType.REMOVE)
	private final List<SingleProblemSolver> solvers = new ArrayList<>();

	public static SingleProblemReadModel of(
		final Long singleProblemId,
		final Long problemId,
		final String singleProblemName,
		final String problemImage,
		final String coursePath,
		final AnswerType answerType,
		final Difficulty difficulty,
		final Long firstTrySuccessCount,
		final Long totalAttemptedCount,
		final Long attemptedUserDistinctCount,
		final LocalDateTime createdAt
	) {
		final SingleProblemReadModel model = new SingleProblemReadModel();
		model.setId(singleProblemId);
		model.setProblemImage(problemImage);
		model.setSingleProblemName(singleProblemName);
		model.setProblemId(problemId);
		model.setCoursePath(coursePath);
		model.setAnswerType(answerType);
		model.setDifficulty(difficulty);
		model.setFirstTrySuccessCount(firstTrySuccessCount);
		model.setTotalAttemptedCount(totalAttemptedCount);
		model.setAttemptedUserDistinctCount(attemptedUserDistinctCount);
		model.setCreatedAt(createdAt);
		model.setUpdatedAt(createdAt);
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
