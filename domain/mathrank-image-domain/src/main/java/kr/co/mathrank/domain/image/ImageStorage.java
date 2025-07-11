package kr.co.mathrank.domain.image;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.image.exception.NoSuchImageException;
import kr.co.mathrank.domain.image.exception.NotSupportedMediaException;
import kr.co.mathrank.domain.image.exception.StoreException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class ImageStorage {
	@Value("${server.local.storage}")
	private String storagePath;

	public void store(final UploadFile uploadFile) {
		final Path path = resolveFilePath(uploadFile.fullFileName());
		try (final OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW)) {
			uploadFile.inputStream().transferTo(outputStream);
			uploadFile.inputStream().close();
			log.info("[ImageStorage.store] stored image: {}", uploadFile);
		} catch (IOException e) {
			log.error("[ImageStorage.save] error occurred: {}, because: {}", uploadFile, e.getMessage(), e);
			throw new StoreException();
		}
	}

	public void delete(final String fullFileName) {
		log.info("[ImageStorage.delete] delete file: {}", fullFileName);
		final Path absoluteMediaPath = this.resolveFilePath(fullFileName);
		absoluteMediaPath.toFile().delete();
	}

	public ImageFileResult load(final String fullFileName) {
		log.info("[ImageStorage.load] load file: {}", fullFileName);
		final Path absoluteImagePath = this.resolveFilePath(fullFileName);
		Resource fileResource = new FileSystemResource(absoluteImagePath);
		if (!fileResource.exists()) {
			log.error("[ImageStorage.load] File not found: {}", absoluteImagePath);
			throw new NoSuchImageException();
		}

		try {
			final Path path = fileResource.getFile().toPath();
			return new ImageFileResult(fileResource.getInputStream(), MediaType.parseMediaType(
				Files.probeContentType(path)), fileResource.contentLength());
		} catch (IOException | InvalidMediaTypeException | InvalidPathException | SecurityException e) {
			log.error("[ImageStorage.load] not supported mediaType: {}", fullFileName, e);
			throw new NotSupportedMediaException();
		}
	}

	protected final Path resolveFilePath(final String fileName) {
		return Paths.get(storagePath)
			.toAbsolutePath()
			.normalize()
			.resolve(fileName);
	}
}
