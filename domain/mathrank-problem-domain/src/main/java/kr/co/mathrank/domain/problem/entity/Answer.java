package kr.co.mathrank.domain.problem.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "answerContent")
@ToString(exclude = "problem")
public class Answer implements Persistable<Long> {
	@Id
	private Long id;

	@ManyToOne
	private Problem problem;

	private String answerContent;

	@CreationTimestamp
	private LocalDateTime createdAt;

	public static Answer of(final Long id, final String answerContent, final Problem problem) {
		final Answer answer = new Answer();
		answer.id = id;
		answer.answerContent = answerContent;
		answer.problem = problem;

		return answer;
	}

	@Override
	public boolean isNew() {
		log.debug("[Answer.isNew] id: {}, isNew: {}", id, createdAt);
		return createdAt == null;
	}
}
