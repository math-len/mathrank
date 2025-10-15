package kr.co.mathrank.domain.contents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @param memberId
 * @param contentId
 * @param idempotencyKey 중복 결제 방지를 위한 키
 */
public record ContentOrderCommand(
	@NotNull
	Long memberId,
	@NotNull
	Long contentId,
	@NotBlank
	String idempotencyKey
) {
}
