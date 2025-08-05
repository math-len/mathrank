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

	private Long firstTrySuccessCount = 0L; // 첫번째 시도에서 성공한 횟수

	private Long totalAttemptedCount = 0L; // 문제를 풀려고 시도한 총 횟수

	private Long attemptedUserDistinctCount = 0L; // 해당 문제를 풀려고 한 사용자 수

	@CreationTimestamp
	private LocalDateTime singleProblemRegisteredAt;

	public void firstTry(final boolean success) {
		// 첫 시도에 문제 풀이 성공 시
		if (success) {
			firstTrySuccessCount++;
		}

		// 성공여부와 상관없이 첫 시도라면 항상 증가
		totalAttemptedCount++;
		attemptedUserDistinctCount++;
	}

	public void increaseAttemptCount() {
		totalAttemptedCount++;
	}

	public static SingleProblem of(final Long problemId, final Long memberId) {
		final SingleProblem problem = new SingleProblem();
		problem.problemId = problemId;
		problem.memberId = memberId;

		return problem;
	}
}
