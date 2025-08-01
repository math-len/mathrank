package kr.co.mathrank.domain.problem.single.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
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

	private Long problemId;

	private Long memberId;

	@OneToMany(mappedBy = "singleProblem", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<Challenger> challengers = new ArrayList<>();

	private Long challengerCount = 0L;

	private Long firstTrySuccessCount = 0L;

	private Double accuracy = 0.0D;

	@CreationTimestamp
	private LocalDateTime singleProblemRegisteredAt;

	public static SingleProblem of(final Long problemId, final Long memberId) {
		final SingleProblem problem = new SingleProblem();
		problem.problemId = problemId;
		problem.memberId = memberId;

		return problem;
	}
}
