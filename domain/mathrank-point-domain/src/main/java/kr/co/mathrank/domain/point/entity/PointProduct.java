package kr.co.mathrank.domain.point.entity;

import java.math.BigDecimal;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Convert(converter = BigDecimalConverter.class)
	private BigDecimal pointAmount;

	@Enumerated(EnumType.STRING)
	private Currency currency;

	@Convert(converter = BigDecimalConverter.class)
	private BigDecimal price;

	public static PointProduct of(final BigDecimal pointAmount, final Currency currency, final BigDecimal price) {
		final PointProduct purchasablePoint = new PointProduct();
		purchasablePoint.pointAmount = pointAmount;
		purchasablePoint.currency = currency;
		purchasablePoint.price = price;

		return purchasablePoint;
	}
}
