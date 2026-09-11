package kr.co.mathrank.domain.auth.client;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

class OAuthClientManagerTest {

	@Test
	void routesLoginToRequestedCredentialProfile() {
		final FakeOAuthClient legacy = new FakeOAuthClient(OAuthCredentialProfile.LEGACY, "legacy-user");
		final FakeOAuthClient primary = new FakeOAuthClient(OAuthCredentialProfile.PRIMARY, "primary-user");
		final OAuthClientManager manager = new OAuthClientManager(List.of(legacy, primary));

		final MemberInfo memberInfo = manager.getMemberInfo(new OAuthLoginCommand(
			"code",
			"state",
			OAuthProvider.GOOGLE,
			OAuthCredentialProfile.PRIMARY
		));

		Assertions.assertEquals("primary-user", memberInfo.memberId());
		Assertions.assertEquals(0, legacy.loginCount);
		Assertions.assertEquals(1, primary.loginCount);
	}

	@Test
	void omittedCredentialProfileUsesLegacyForOldApps() {
		final FakeOAuthClient legacy = new FakeOAuthClient(OAuthCredentialProfile.LEGACY, "legacy-user");
		final FakeOAuthClient primary = new FakeOAuthClient(OAuthCredentialProfile.PRIMARY, "primary-user");
		final OAuthClientManager manager = new OAuthClientManager(List.of(legacy, primary));

		final MemberInfo memberInfo = manager.getMemberInfo(
			new OAuthLoginCommand("code", "state", OAuthProvider.KAKAO)
		);

		Assertions.assertEquals("legacy-user", memberInfo.memberId());
		Assertions.assertEquals(1, legacy.loginCount);
		Assertions.assertEquals(0, primary.loginCount);
	}

	private static final class FakeOAuthClient implements OAuthClientHandler {
		private final OAuthCredentialProfile profile;
		private final String memberId;
		private int loginCount;

		private FakeOAuthClient(final OAuthCredentialProfile profile, final String memberId) {
			this.profile = profile;
			this.memberId = memberId;
		}

		@Override
		public MemberInfoResponse getMemberInfo(final OAuthLoginCommand command) {
			loginCount++;
			return () -> new MemberInfo(memberId, "tester", null, null, null);
		}

		@Override
		public boolean supports(final OAuthProvider provider, final OAuthCredentialProfile credentialProfile) {
			return provider == OAuthProvider.GOOGLE || provider == OAuthProvider.KAKAO
				? profile == credentialProfile
				: false;
		}

		@Override
		public boolean revoke(final String refreshToken) {
			return true;
		}
	}
}
