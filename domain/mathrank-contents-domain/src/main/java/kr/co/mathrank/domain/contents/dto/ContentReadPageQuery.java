package kr.co.mathrank.domain.contents.dto;

import kr.co.mathrank.domain.contents.entity.ContentType;

public record ContentReadPageQuery(
	String title,
	ContentType contentType
) {
}
