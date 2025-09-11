package kr.co.mathrank.domain.image;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLConnection;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.image.exception.NoSuchImageException;
import kr.co.mathrank.domain.image.exception.StoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@RequiredArgsConstructor
class AwsImageStorage implements ImageStorage {
	private static final String BUCKET_NAME = "mathrank-image";
	private final S3Client s3Client;

	@Override
	public void store(UploadFile uploadFile) {
		try (final BufferedInputStream inputStream = new BufferedInputStream(uploadFile.inputStream())) {
			s3Client.putObject(
				PutObjectRequest.builder()
					.bucket(BUCKET_NAME)
					.key(uploadFile.fullFileName())
					.build(),
				RequestBody.fromBytes(inputStream // 현재 스펙 상 크기 모름
				.readAllBytes()));
			log.info("[AwsImageStorage.store] store file succeed - uploadFileName: {}", uploadFile.fullFileName());
		} catch (IOException e) {
			log.error("[AwsImageStorage.store] store file failed - uploadFileName: {}", uploadFile.fullFileName(), e);
			throw new StoreException("파일 저장 불가");
		}
	}

	@Override
	public void delete(String fullFileName) {
		final DeleteObjectResponse response = s3Client.deleteObject(builder -> builder.bucket(BUCKET_NAME).key(fullFileName));
		if (response.sdkHttpResponse().isSuccessful()) {
			log.info("[AwsImageStorage.delete] delete image succeeded - fullFileName = {}", fullFileName);
			return;
		}
		log.info("[AwsImageStorage.delete] delete image failed - fullFileName = {}", fullFileName);
	}

	@Override
	public ImageFileResult load(String fullFileName) {
		try {
			final ResponseInputStream<GetObjectResponse> responseInputStream  = s3Client.getObject(GetObjectRequest.builder()
				.bucket(BUCKET_NAME)
				.key(fullFileName)
				.build());

			return new ImageFileResult(
				responseInputStream,
				MediaType.parseMediaType(URLConnection.guessContentTypeFromName(fullFileName)),
				responseInputStream.response().contentLength()
			);
		} catch (NoSuchKeyException e) {
			log.info("[AwsImageStorage.load] load image failed - key: {}", fullFileName);
			throw new NoSuchImageException("이미지를 찾을 수 없음: " + fullFileName);
		}

	}
}
