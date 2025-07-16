package kr.co.mathrank.app.api.image;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.domain.image.ImageFileResult;
import kr.co.mathrank.domain.image.ImageService;
import kr.co.mathrank.domain.image.UploadImageCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "이미지 API")
@RestController
@RequiredArgsConstructor
public class ImageController {
	private final ImageService imageService;

	@Operation(summary = "이미지 조회 API", description = "댓글 및 게시글 조회시 받은 이미지 주소를 포함하여 요청하면 이미지 데이터를 리턴합니다.")
	@GetMapping("/api/v1/image")
	public ResponseEntity<byte[]> getImage(@RequestParam final String imageSource) throws IOException {
		final ImageFileResult media = imageService.load(imageSource);

		return ResponseEntity.ok()
			.contentType(media.mediaType())
			.body(media.data().readAllBytes());
	}

	@Operation(summary = "이미지 업로드 API", description = "multipartForm으로 업로드. 업로드 후 응답받은 string 값을 통해 이미지를 로드 가능합니다.")
	@PostMapping(value = "/api/v1/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Authorization(openedForAll = true)
	public ResponseEntity<Response.ImageResponse> postImage(
		@NotNull final MultipartFile image
	) {
		final String originalFileName = image.getOriginalFilename();
		UploadImageCommand command;
		try {
			command = new UploadImageCommand(image.getInputStream(), getFileNameWithoutExtension(originalFileName), parseExtension(originalFileName));
		} catch (IOException e) {
			log.error("[ImageController.postImage] cannot get inputStream: {}", e.getMessage(), e);
			throw new RuntimeException(e);
		}
		final String imageSource = imageService.store(command);

		return ResponseEntity.ok(new Response.ImageResponse(imageSource));
	}

	private String getFileNameWithoutExtension(final String fullFileName) {
		final int extensionIdx = fullFileName.lastIndexOf(".");
		if (extensionIdx == -1) {
			throw new IllegalArgumentException();
		}
		return fullFileName.substring(0, extensionIdx);
	}

	private String parseExtension(final String fullFileName) {
		final int extensionIdx = fullFileName.lastIndexOf(".");
		if (extensionIdx == -1) {
			throw new IllegalArgumentException();
		}
		return fullFileName.substring(extensionIdx + 1);
	}
}
