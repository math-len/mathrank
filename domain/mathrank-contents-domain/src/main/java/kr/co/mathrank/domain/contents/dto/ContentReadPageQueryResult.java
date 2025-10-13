package kr.co.mathrank.domain.contents.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import kr.co.mathrank.domain.contents.entity.Content;
import kr.co.mathrank.domain.contents.entity.ContentType;

public record ContentReadPageQueryResult(
	Long contentId,
	String title,
	ContentType contentType,
	BigDecimal price,
	LocalDateTime createdAt
) {
	public static ContentReadPageQueryResult from(final Content content) {
		return new ContentReadPageQueryResult(
			content.getId(),
			content.getTitle(),
			content.getContentType(),
			content.getPrice(),
			content.getCreatedAt()
		);
	}
}
