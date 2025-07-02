package kr.co.mathrank.domain.problem.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem implements Persistable<Long> {
	@Id
	private Long id;

	private Long memberId;

	private String imageSource;

	@Enumerated(EnumType.STRING)
	private Difficulty difficulty;

	@Enumerated(EnumType.STRING)
	private ProblemCourse course;

	@Enumerated(EnumType.STRING)
	private AnswerType type;

	private String schoolCode;

	private String answer;

	@CreationTimestamp
	private LocalDateTime createdAt;

	public static Problem of(final Long id, final Long memberId, final String imageSource, final Difficulty difficulty,
		final AnswerType type, final ProblemCourse course, final String answer, final String schoolCode) {

		final Problem problem = new Problem();
		problem.id = id;
		problem.memberId = memberId;
		problem.imageSource = imageSource;
		problem.difficulty = difficulty;
		problem.type = type;
		problem.course = course;
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
