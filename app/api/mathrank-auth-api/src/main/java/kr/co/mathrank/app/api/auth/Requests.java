package kr.co.mathrank.app.api.auth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.app.api.auth.serializer.PasswordDeserializer;
import kr.co.mathrank.domain.auth.dto.LoginCommand;
import kr.co.mathrank.domain.auth.entity.Password;

class Requests {
	record LoginRequest(
		@NotNull
		String loginId,
		@NotNull
		@JsonDeserialize(using = PasswordDeserializer.class)
		@Schema(type = "string", format = "password")
		Password password
	) {
		LoginCommand toCommand() {
			return new LoginCommand(loginId, password);
		}
	}
}
