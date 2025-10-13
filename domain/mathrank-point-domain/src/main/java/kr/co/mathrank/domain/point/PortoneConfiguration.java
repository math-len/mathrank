package kr.co.mathrank.domain.point;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.portone.sdk.server.payment.PaymentClient;
import lombok.Getter;
import lombok.Setter;

@Configuration
public class PortoneConfiguration {
	@Bean
	PaymentClient paymentClient(final PortoneConfigurationProperties properties) {
		return new PaymentClient(properties.getApiSecret(), properties.getApiBase(), properties.getStoreId());
	}

	@Getter
	@Setter
	@ConfigurationProperties("payment.portone")
	@Configuration
	static class PortoneConfigurationProperties {
		private String apiSecret;
		private String apiBase;
		private String storeId;
	}
}
