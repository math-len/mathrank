package kr.co.mathrank.domain.image;

import java.io.InputStream;

import org.springframework.http.MediaType;

public record ImageFileResult(
	InputStream data,
	MediaType mediaType,
	Long fileSize
) {
}
