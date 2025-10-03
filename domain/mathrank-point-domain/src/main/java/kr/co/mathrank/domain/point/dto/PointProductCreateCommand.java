package kr.co.mathrank.domain.point.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.point.entity.Currency;
import kr.co.mathrank.domain.point.entity.PointProduct;

public record PointProductCreateCommand(
	@NotNull
	BigDecimal pointAmount,
	@NotNull
	Currency currency,
	@NotNull
	BigDecimal price
) {
	public PointProduct toEntity() {
		return PointProduct.of(pointAmount, currency, price);
	}
}
