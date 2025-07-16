package kr.co.mathrank;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.service.OpenAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfiguration {

	@Bean
	public OpenApiCustomizer customOpenAPI() {
		return openAPIBuilder -> {
			openAPIBuilder.addServersItem(new Server().url("https://rank.hpmath.co.kr"));
		};
	}
}
