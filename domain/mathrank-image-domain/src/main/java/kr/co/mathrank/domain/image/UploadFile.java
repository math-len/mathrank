package kr.co.mathrank.domain.image;

import java.io.InputStream;

public record UploadFile(
	String fullFileName,
	InputStream inputStream
) {
}
