package kr.co.mathrank.app.api.common.docs;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import kr.co.mathrank.app.api.common.authentication.Authorization;

@Configuration
public class CommonOpenApiConfiguration {
	private static final String SECURITY_SCHEME_NAME = "JWT";
	@Value("${server.url:http://localhost}")
	private String url;
	@Value("${server.version:test}")
	private String tagVersion;

	@Bean
	public OpenAPI addSecurityComponent() {
		return new OpenAPI()
			.info(new Info()
				.title("매쓰랭 API")
				.version(tagVersion) // 여기서 버전 변경
				.description("API 문서 설명"))
			.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
				new SecurityScheme()
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
			));
	}

	@Bean
	public OperationCustomizer customizeSecurityForCustomAnnotation() {
		return (operation, handlerMethod) -> {

			// 커스텀 어노테이션 감지 (메서드)
			boolean hasAuthAnnotation = handlerMethod
				.getMethod()
				.isAnnotationPresent(Authorization.class);

			if (hasAuthAnnotation) {
				operation.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
			}

			return operation;
		};
	}

	@Bean
	public OpenApiCustomizer addDeployUrl() {
		return openAPIBuilder -> {
			openAPIBuilder.addServersItem(new Server().url(url));
		};
	}
}
