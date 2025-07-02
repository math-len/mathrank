package kr.co.mathrank.domain.problem.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "problem", indexes = {
	@Index(name = "idx_member_id", columnList = "member_id"),
	@Index(name = "idx_difficulty", columnList = "difficulty"),
	@Index(name = "idx_problem_course", columnList = "problem_course"),
	@Index(name = "idx_type", columnList = "type"),
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem implements Persistable<Long> {
	@Id
	@Setter(AccessLevel.NONE)
	private Long id;

	@Setter(AccessLevel.NONE)
	private Long memberId;

	private String imageSource;

	@Enumerated(EnumType.STRING)
	private Difficulty difficulty;

	@Enumerated(EnumType.STRING)
	private ProblemCourse problemCourse;

	@Enumerated(EnumType.STRING)
	private AnswerType type;

	private String schoolCode;

	private String answer;

	@CreationTimestamp
	@Setter(AccessLevel.NONE)
	private LocalDateTime createdAt;

	public static Problem of(final Long id, final Long memberId, final String imageSource, final Difficulty difficulty,
		final AnswerType type, final ProblemCourse course, final String answer, final String schoolCode) {

		final Problem problem = new Problem();
		problem.id = id;
		problem.memberId = memberId;
		problem.imageSource = imageSource;
		problem.difficulty = difficulty;
		problem.type = type;
		problem.problemCourse = course;
		problem.answer = answer;
		problem.schoolCode = schoolCode;

		return problem;
	}

	@Override
	public boolean isNew() {
		log.debug("[Problem.isNew] id: {}, isNew: {}", id, createdAt);
		return this.createdAt == null;
	}
}
