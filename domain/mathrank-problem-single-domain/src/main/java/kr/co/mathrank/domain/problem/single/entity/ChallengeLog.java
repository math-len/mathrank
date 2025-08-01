package kr.co.mathrank.domain.problem.single.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeLog {
	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Challenger challenger;

	private Boolean success;

	@CreationTimestamp
	private LocalDateTime challengeAt;
}
