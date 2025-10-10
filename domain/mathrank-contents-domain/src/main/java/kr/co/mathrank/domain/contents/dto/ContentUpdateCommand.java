package kr.co.mathrank.domain.contents.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.contents.entity.UploadFileInfo;

public record ContentUpdateCommand(
	@NotNull
	Long contentId,
	@NotNull
	String title,
	@NotNull
	String text,
	@NotNull
	BigDecimal price,
	List<ContentFileRegisterCommand> fileSources,
	List<String> videoLinks
) {
	public List<UploadFileInfo> getUploadFileInfos() {
		return fileSources.stream()
			.map(ContentFileRegisterCommand::toEntity)
			.toList();
	}
}
