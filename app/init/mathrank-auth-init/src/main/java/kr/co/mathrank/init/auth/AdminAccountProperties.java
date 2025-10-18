package kr.co.mathrank.init.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "mathrank.admin")
@Getter
@Setter
public class AdminAccountProperties {
	private Map<String, Account> account = new HashMap<>();

	@Getter
	@Setter
	public static class Account {
		private String loginId;
		private String password;
	}
}
