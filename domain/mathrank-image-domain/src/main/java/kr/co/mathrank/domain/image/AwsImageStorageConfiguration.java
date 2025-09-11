package kr.co.mathrank.domain.image;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
class AwsImageStorageConfiguration {
	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.region(Region.AP_NORTHEAST_2) // 서울 리전
			.credentialsProvider(InstanceProfileCredentialsProvider.create())
			.build();
	}
}
