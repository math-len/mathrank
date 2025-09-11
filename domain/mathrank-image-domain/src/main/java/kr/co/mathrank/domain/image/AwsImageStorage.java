package kr.co.mathrank.domain.image;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLConnection;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
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
				RequestBody.fromBytes(inputStream
				.readAllBytes()));
			log.info("[AwsImageStorage.store] store file succeed - uploadFileName: {}", uploadFile.fullFileName());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(String fullFileName) {
		final DeleteObjectResponse response = s3Client.deleteObject(builder -> builder.bucket(BUCKET_NAME).key(fullFileName));
		if (response.deleteMarker()) {
			log.info("[AwsImageStorage.delete] delete image succeeded - fullFileName = {}", fullFileName);
			return;
		}
		log.info("[AwsImageStorage.delete] delete image failed - fullFileName = {}", fullFileName);
	}

	@Override
	public ImageFileResult load(String fullFileName) {
		final ResponseInputStream<GetObjectResponse> responseInputStream  = s3Client.getObject(GetObjectRequest.builder()
			.bucket(BUCKET_NAME)
			.key(fullFileName)
			.build());

		return new ImageFileResult(
			responseInputStream,
			MediaType.parseMediaType(URLConnection.guessContentTypeFromName(fullFileName)),
			responseInputStream.response().contentLength()
		);
	}
}
