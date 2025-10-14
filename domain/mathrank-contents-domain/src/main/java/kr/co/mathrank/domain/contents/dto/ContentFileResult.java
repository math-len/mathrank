package kr.co.mathrank.domain.contents.dto;

import kr.co.mathrank.domain.contents.entity.UploadFileInfo;

public record ContentFileResult(
	String fileRealName,
	String fileSource
) {
	public static ContentFileResult of(final UploadFileInfo uploadFileInfo) {
		return new ContentFileResult(uploadFileInfo.getFileName(), uploadFileInfo.getFileSource());
	}
}
