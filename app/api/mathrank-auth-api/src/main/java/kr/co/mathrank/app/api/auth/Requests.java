package kr.co.mathrank.app.api.auth;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.app.api.auth.serializer.PasswordDeserializer;
import kr.co.mathrank.domain.auth.dto.LoginCommand;
import kr.co.mathrank.domain.auth.dto.MemberInfoCompleteCommand;
import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.MemberType;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;
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

	record OAuthLoginRequest(
		@NotNull
		String code,
		@NotNull
		String state,
		@Schema(description = "OAuth 자격 증명 프로필. 생략 시 기존 앱 호환용 LEGACY")
		OAuthCredentialProfile credentialProfile
	) {
		public OAuthLoginCommand toCommand(final OAuthProvider provider) {
			return new OAuthLoginCommand(code, state, provider, credentialProfile);
		}
	}

	public record MemberRegistrationCompleteRequest(
		@NotNull
		MemberType memberType,
		@NotNull
		String nickName,
		String schoolCode,
		@NotNull
		Boolean agreeToPrivacyPolicy
	) {
		public MemberInfoCompleteCommand toCommand(final Long memberId) {
			return new MemberInfoCompleteCommand(memberId, nickName, memberType, schoolCode, agreeToPrivacyPolicy);
		}
	}
}
