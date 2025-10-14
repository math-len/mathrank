package kr.co.mathrank.domain.contents.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.domain.contents.entity.Content;
import kr.co.mathrank.domain.contents.entity.ContentType;

public record ContentReadQueryResult(
	Long contentId,
	Long contentOwnerId,
	String title,
	String text,
	ContentType contentType,
	List<ContentFileResult> fileInfos,
	List<String> videoLinks,
	LocalDateTime registeredAt
) {
	public static ContentReadQueryResult from(final Content content) {
		return new ContentReadQueryResult(
			content.getId(),
			content.getOwnerId(),
			content.getTitle(),
			content.getText(),
			content.getContentType(),
			content.getFiles().stream()
				.map(ContentFileResult::of)
				.toList(),
			content.getVideoLinks(),
			content.getCreatedAt()
		);
	}
}
