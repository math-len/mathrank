package kr.co.mathrank.domain.problem.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "problem", indexes = {
	@Index(name = "idx_member_id", columnList = "member_id"),
	@Index(name = "idx_difficulty", columnList = "difficulty"),
	@Index(name = "idx_type", columnList = "type"),
	@Index(name = "idx_videoLink", columnList = "solution_video_link")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Problem implements Persistable<Long> {
	@Id
	@Setter(AccessLevel.NONE)
	private Long id;

	@Setter(AccessLevel.NONE)
	private Long memberId;

	private String problemImage;

	private String solutionImage;

	@Convert(converter = DifficultyConverter.class)
	private Difficulty difficulty;

	@Enumerated(EnumType.STRING)
	private AnswerType type;

	@ManyToOne(fetch = FetchType.LAZY)
	private Course course;

	private String schoolCode;

	private String location;

	private Integer years;

	private String solutionVideoLink;

	@OneToMany(mappedBy = "problem", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	@BatchSize(size = 100)
	private final Set<Answer> answers = new HashSet<>();

	@CreationTimestamp
	@Setter(AccessLevel.NONE)
	private LocalDateTime createdAt;

	private String memo;

	public static Problem of(final Long id, final Long memberId, final String problemImage, final Difficulty difficulty,
		final AnswerType type, final Course course, final String schoolCode,
		final String solutionVideoLink, final String solutionImage, final Integer year, final String location, final String memo) {

		final Problem problem = new Problem();
		problem.id = id;
		problem.memberId = memberId;
		problem.problemImage = problemImage;
		problem.difficulty = difficulty;
		problem.type = type;
		problem.course = course;
		problem.schoolCode = schoolCode;
		problem.solutionVideoLink = solutionVideoLink;
		problem.solutionImage = solutionImage;
		problem.years = year;
		problem.location = location;
		problem.memo = memo;

		return problem;
	}

	public void setAnswers(final Set<Answer> answers) {
		clearAnswer();
		this.answers.addAll(answers);
	}

	private void clearAnswer() {
		this.answers.clear();
	}

	@Override
	public boolean isNew() {
		log.debug("[Problem.isNew] id: {}, isNew: {}", id, createdAt);
		return this.createdAt == null;
	}

	public Set<String> getAnswers() {
		return this.answers.stream()
			.map(Answer::getAnswerContent)
			.collect(Collectors.toSet());
	}
}
