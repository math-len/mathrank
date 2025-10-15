package kr.co.mathrank.domain.contents.dto;

import jakarta.validation.constraints.NotNull;

public record ContentOrderQuery(
	Long memberId
) {
}
