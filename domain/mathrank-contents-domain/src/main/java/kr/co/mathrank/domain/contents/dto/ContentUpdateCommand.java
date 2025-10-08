package kr.co.mathrank.domain.contents.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public record ContentUpdateCommand(
	@NotNull
	Long contentId,
	@NotNull
	String title,
	@NotNull
	String text,
	@NotNull
	BigDecimal price,
	List<String> fileSources,
	List<String> videoLinks
) {
}
