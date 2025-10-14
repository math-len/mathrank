package kr.co.mathrank.app.api.point;

import kr.co.mathrank.domain.point.dto.PointProductQueryResult;
import kr.co.mathrank.domain.point.entity.Currency;

public class Responses {
	record PointProductQueryResponse(
		Long pointProductId,
		Long pointAmount,
		Currency currency,
		Long price
	) {
		public static PointProductQueryResponse from(final PointProductQueryResult result) {
			return new PointProductQueryResponse(
				result.pointProductId(),
				result.pointAmount().longValue(),
				result.currency(),
				result.price().longValue()
			);
		}
	}
}
