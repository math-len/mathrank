package kr.co.mathrank.app.api.point;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.point.dto.PointProductCreateCommand;
import kr.co.mathrank.domain.point.entity.Currency;

public class Requests {
	record PointProductRegisterRequest(
		@NotNull
		Long pointAmount,
		@NotNull
		Currency currency,
		@NotNull
		Long price
	) {
		public PointProductCreateCommand toCommand() {
			return new PointProductCreateCommand(
				BigDecimal.valueOf(pointAmount()),
				currency(),
				BigDecimal.valueOf(price())
			);
		}
	}
}
