package kr.co.mathrank.domain.problem.single.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SingleProblem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private Long problemId;

	private Long memberId;

	@OneToMany(mappedBy = "singleProblem", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<ChallengeLog> challengeLogs = new ArrayList<>();

	private Long firstTrySuccessCount = 0L;

	@CreationTimestamp
	private LocalDateTime singleProblemRegisteredAt;

	public Long increaseFirstTrySuccessCount() {
		return ++firstTrySuccessCount;
	}

	public static SingleProblem of(final Long problemId, final Long memberId) {
		final SingleProblem problem = new SingleProblem();
		problem.problemId = problemId;
		problem.memberId = memberId;

		return problem;
	}
}
