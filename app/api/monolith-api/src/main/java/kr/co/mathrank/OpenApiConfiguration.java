package kr.co.mathrank;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfiguration {
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.servers(List.of(new Server().url("https://rank.hpmath.co.kr")));
	}
}
