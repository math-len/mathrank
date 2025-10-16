package kr.co.mathrank.domain.contents.dto;

import kr.co.mathrank.domain.contents.entity.OrderStatus;

public record ContentOrderQuery(
	Long memberId,
	OrderStatus orderStatus
) {
}
