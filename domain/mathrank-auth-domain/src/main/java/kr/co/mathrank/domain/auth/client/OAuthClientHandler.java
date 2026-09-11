package kr.co.mathrank.domain.auth.client;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

interface OAuthClientHandler {
	MemberInfoResponse getMemberInfo(final OAuthLoginCommand command);
	boolean supports(final OAuthProvider provider, final OAuthCredentialProfile credentialProfile);
	default boolean supports(final OAuthProvider provider) {
		return supports(provider, OAuthCredentialProfile.LEGACY)
			|| supports(provider, OAuthCredentialProfile.PRIMARY);
	}
	boolean revoke(final String refreshToken);
}
