package kr.co.mathrank.domain.contents.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class ContentPurchaseLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	private Content content;

	private Long purchasedPointAmount;

	private LocalDateTime purchasedAt;
}
