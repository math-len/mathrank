package kr.co.mathrank.app.api.contents;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.contents.constraints.ValidationGroup;
import kr.co.mathrank.domain.contents.dto.ContentFileRegisterCommand;
import kr.co.mathrank.domain.contents.dto.ContentRegisterCommand;
import kr.co.mathrank.domain.contents.dto.ContentUpdateCommand;
import kr.co.mathrank.domain.contents.entity.ContentType;

public class Requests {
	record ContentRegisterRequest(
		@NotBlank
		String title,
		@NotNull
		String text,
		@NotNull
		ContentType contentType,
		@NotNull
		@Valid
		List<ContentFileRegisterCommand> files,
		@NotNull
		Long price,
		@NotNull(groups = ValidationGroup.VideoContentConstraints.class)
		List<String> videoLinks // contentType 이 VIDEO 인 경우 적용.
	) {
		public ContentRegisterCommand toCommand() {
			return new ContentRegisterCommand(
				title,
				text,
				contentType,
				files,
				BigDecimal.valueOf(price),
				videoLinks
			);
		}
	}

	record ContentUpdateRequest(
		@NotNull
		String title,
		@NotNull
		String text,
		@NotNull
		Long price,
		List<ContentFileRegisterCommand> fileSources,
		List<String> videoLinks
	) {
		public ContentUpdateCommand toCommand(final Long contentId) {
			return new ContentUpdateCommand(
				contentId,
				title,
				text,
				BigDecimal.valueOf(price),
				fileSources,
				videoLinks
			);
		}
	}
}
