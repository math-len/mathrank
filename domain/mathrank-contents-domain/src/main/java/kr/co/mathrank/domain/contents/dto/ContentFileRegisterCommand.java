package kr.co.mathrank.domain.contents.dto;

import jakarta.validation.constraints.NotBlank;
import kr.co.mathrank.domain.contents.entity.UploadFileInfo;

public record ContentFileRegisterCommand(
	@NotBlank
	String fileName,
	@NotBlank
	String fileSource
) {
	public UploadFileInfo toEntity() {
		return UploadFileInfo.of(fileName, fileSource);
	}
}
