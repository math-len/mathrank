package kr.co.mathrank.domain.contents.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.contents.entity.Content;
import kr.co.mathrank.domain.contents.entity.ContentType;

public record ContentReadQueryResult(
	Long contentId,
	Long contentOwnerId,
	ContentType contentType,
	List<String> fileSources,
	List<String> videoLinks,
	LocalDateTime registeredAt
) {
	public static ContentReadQueryResult from(final Content content) {
		return new ContentReadQueryResult(
			content.getId(),
			content.getOwnerId(),
			content.getContentType(),
			content.getFileSources(),
			content.getVideoLinks(),
			content.getCreatedAt()
		);
	}
}
