package kr.co.mathrank.domain.auth.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthCredentialProfile;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

class NaverOAuthClientTest {

	@Test
	void exchangesCodeWithFormBodyAndUsesRawAccessTokenAsBearerCredential() {
		final RestClient.Builder tokenBuilder = RestClient.builder().baseUrl("https://nid.test/token");
		final RestClient.Builder infoBuilder = RestClient.builder().baseUrl("https://openapi.test/me");
		final MockRestServiceServer tokenServer = MockRestServiceServer.bindTo(tokenBuilder).build();
		final MockRestServiceServer infoServer = MockRestServiceServer.bindTo(infoBuilder).build();
		final NaverOAuthClient client = new NaverOAuthClient(properties(), OAuthCredentialProfile.PRIMARY,
			tokenBuilder.build(), infoBuilder.build());

		tokenServer.expect(method(HttpMethod.POST))
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(content().string("grant_type=authorization_code&client_id=client-id&client_secret=secret"
				+ "&code=auth-code&state=csrf-state"))
			.andRespond(withSuccess("""
				{"access_token":"token+with/slash=","refresh_token":"refresh-token","token_type":"bearer",
				 "expires_in":"3600"}
				""", MediaType.APPLICATION_JSON));
		infoServer.expect(method(HttpMethod.GET))
			.andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token+with/slash="))
			.andRespond(withSuccess("""
				{"resultcode":"00","message":"success",
				 "response":{"id":"member-id","nickname":"nick","email":"member@example.com"}}
				""", MediaType.APPLICATION_JSON));

		final MemberInfo info = client.getMemberInfo(new OAuthLoginCommand(
			"auth-code", "csrf-state", OAuthProvider.NAVER, OAuthCredentialProfile.PRIMARY)).toInfo();

		assertThat(info.memberId()).isEqualTo("member-id");
		assertThat(info.refreshToken()).isEqualTo("refresh-token");
		tokenServer.verify();
		infoServer.verify();
	}

	@Test
	void rejectsSuccessfulHttpResponseWithoutAccessTokenBeforeProfileRequest() {
		final RestClient.Builder tokenBuilder = RestClient.builder().baseUrl("https://nid.test/token");
		final RestClient.Builder infoBuilder = RestClient.builder().baseUrl("https://openapi.test/me");
		final MockRestServiceServer tokenServer = MockRestServiceServer.bindTo(tokenBuilder).build();
		final NaverOAuthClient client = new NaverOAuthClient(properties(), OAuthCredentialProfile.PRIMARY,
			tokenBuilder.build(), infoBuilder.build());

		tokenServer.expect(method(HttpMethod.POST))
			.andRespond(withSuccess("""
				{"error":"invalid_request","error_description":"no valid data in session"}
				""", MediaType.APPLICATION_JSON));

		assertThatThrownBy(() -> client.getMemberInfo(new OAuthLoginCommand(
			"auth-code", "csrf-state", OAuthProvider.NAVER, OAuthCredentialProfile.PRIMARY)))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Naver access token issuance failed: invalid_request");
		tokenServer.verify();
	}

	private NaverOAuthProperties properties() {
		return new NaverOAuthProperties() {
			@Override
			public String getClientId() {
				return "client-id";
			}

			@Override
			public String getClientSecret() {
				return "secret";
			}
		};
	}
}
