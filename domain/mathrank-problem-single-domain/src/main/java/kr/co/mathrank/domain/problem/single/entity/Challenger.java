package kr.co.mathrank.domain.problem.single.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenger {
	@Id
	private Long id;

	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	private SingleProblem singleProblem;

	@OneToMany(mappedBy = "challenger", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<ChallengeLog> challengeLogs = new ArrayList<>();

	private LocalDateTime firstTryAt;

	private Boolean successAtFirstChallenge;

	private Boolean eventuallySuccess;
}
