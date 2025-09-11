package kr.co.mathrank.domain.image;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
class ImageStorageConfiguration {
	@Bean
	@Profile("local")
	LocalImageStorage localImageStorage() {
		log.info("[ImageStorageConfiguration.localImageStorage] initialized");
		return new LocalImageStorage();
	}

	@Bean
	@ConditionalOnMissingBean(ImageStorage.class)
	AwsImageStorage awsImageStorage() {
		log.info("[ImageStorageConfiguration.awsImageStorage] initialized");
		return new AwsImageStorage(s3Client());
	}

	S3Client s3Client() {
		return S3Client.builder()
			.region(Region.AP_NORTHEAST_2) // 서울 리전
			.credentialsProvider(InstanceProfileCredentialsProvider.create())
			.build();
	}
}
