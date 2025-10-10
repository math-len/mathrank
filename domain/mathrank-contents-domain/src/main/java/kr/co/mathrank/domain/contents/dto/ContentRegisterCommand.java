package kr.co.mathrank.domain.contents.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.validator.group.GroupSequenceProvider;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.contents.constraints.ContentValidationGroupProvider;
import kr.co.mathrank.domain.contents.constraints.ValidationGroup;
import kr.co.mathrank.domain.contents.entity.Content;
import kr.co.mathrank.domain.contents.entity.ContentType;

@GroupSequenceProvider(ContentValidationGroupProvider.class)
public record ContentRegisterCommand(
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
	BigDecimal price,
	@NotNull(groups = ValidationGroup.VideoContentConstraints.class)
	List<String> videoLinks // contentType 이 VIDEO 인 경우 적용.
) {
	public Content toEntity() {
		return Content.of(
			title(),
			text(),
			files().stream()
				.map(ContentFileRegisterCommand::toEntity)
				.collect(Collectors.toList()),
			videoLinks(),
			price(),
			contentType());
	}
}
