package kr.co.mathrank.domain.point;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.portone.sdk.server.payment.PaymentClient;
import lombok.Getter;
import lombok.Setter;

@Configuration
class PortOneConfiguration {
	@Bean
	PaymentClient paymentClient(final PortOneConfigurationProperties properties) {
		return new PaymentClient(properties.getApiSecret(), properties.getApiBase(), properties.getStoreId());
	}

	@Getter
	@Setter
	@ConfigurationProperties("payment.portone")
	@Configuration
	static class PortOneConfigurationProperties {
		private String apiSecret;
		private String apiBase = "https://api.portone.io";
		private String storeId;
	}
}
