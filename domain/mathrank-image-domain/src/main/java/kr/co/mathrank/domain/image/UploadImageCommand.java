package kr.co.mathrank.domain.image;

import java.io.InputStream;

import jakarta.validation.constraints.NotNull;

public record UploadImageCommand(
	@NotNull
	InputStream inputStream,
	@NotNull
	String fileName,
	@NotNull
	String extension
) {
}
