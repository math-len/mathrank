package kr.co.mathrank.domain.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImageServiceTest {
	@Autowired
	private ImageService imageService;

	@Test
	void 이미지_업로드_테스트() {
		final Path path = Paths.get("src/test/resources/test.jpeg");
		byte[] imageBytes;
		try {
			imageBytes = Files.readAllBytes(path);
			imageService.store(new UploadImageCommand(new ByteArrayInputStream(imageBytes, 0, imageBytes.length), "testFile", "jpeg"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void 이미지_삭제_테스트() {
		final Path path = Paths.get("src/test/resources/test.jpeg");
		byte[] imageBytes;
		String storedFileName;
		try {
			imageBytes = Files.readAllBytes(path);
			storedFileName = imageService.store(new UploadImageCommand(new ByteArrayInputStream(imageBytes, 0, imageBytes.length), "testFile", "jpeg"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		imageService.delete(storedFileName);
	}

	// @Test
	void 이미지_로드_테스트() {
		final Path path = Paths.get("src/test/resources/test.jpeg");
		byte[] imageBytes;
		try {
			imageBytes = Files.readAllBytes(path);
			final String storedName = imageService.store(new UploadImageCommand(new ByteArrayInputStream(imageBytes, 0, imageBytes.length), "testFile", "jpeg"));

			final ImageFileResult result = imageService.load(storedName);
			Assertions.assertNotNull(result);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
