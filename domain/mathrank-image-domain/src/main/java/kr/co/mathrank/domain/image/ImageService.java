package kr.co.mathrank.domain.image;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class ImageService {
	// image-{originalFileName}-{uniqueFileName}.{extension}
	private static final String IMAGE_FILE_NAME_FORMAT = "image-%s-%s.%s";

	private final ImageStorage imageStorage;
	private final Snowflake snowflake;

	public String store(@NotNull @Valid final UploadImageCommand command) {
		final String fullFileName = getUniqueName(command.fileName(), command.extension());
		final UploadFile uploadFile = new UploadFile(fullFileName, command.inputStream());
		imageStorage.store(uploadFile);
		return uploadFile.fullFileName();
	}

	public void delete(@NotNull final String fullFileName) {
		imageStorage.delete(fullFileName);
	}

	public ImageFileResult load(@NotNull final String fullFileName) {
		return imageStorage.load(fullFileName);
	}

	private String getUniqueName(final String originalFileName, final String extension) {
		return IMAGE_FILE_NAME_FORMAT.formatted(originalFileName, snowflake.nextId(), extension);
	}
}
