package kr.co.mathrank.domain.auth.client;

import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;

interface OAuthClientHandler {
	MemberInfoResponse getMemberInfo(final OAuthLoginCommand command);
	boolean supports(final OAuthProvider provider);
}
