package kr.co.mathrank.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

public record OAuthLoginCommand(
	@NotNull
	String code,
	String state,
	@NotNull
	OAuthProvider provider,
	OAuthCredentialProfile credentialProfile
) {
	public OAuthLoginCommand(final String code, final String state, final OAuthProvider provider) {
		this(code, state, provider, OAuthCredentialProfile.LEGACY);
	}

	public OAuthLoginCommand {
		if (credentialProfile == null) {
			credentialProfile = OAuthCredentialProfile.LEGACY;
		}
	}
}
