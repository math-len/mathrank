package kr.co.mathrank.domain.point.dto;

import java.math.BigDecimal;

import kr.co.mathrank.domain.point.entity.Currency;
import kr.co.mathrank.domain.point.entity.PointProduct;

public record PointProductQueryResult(
	Long pointProductId,
	BigDecimal pointAmount,
	Currency currency,
	BigDecimal price
) {
	public static PointProductQueryResult from(PointProduct pointProduct) {
		return new PointProductQueryResult(
			pointProduct.getId(),
			pointProduct.getPointAmount(),
			pointProduct.getCurrency(),
			pointProduct.getPrice()
		);
	}
}
