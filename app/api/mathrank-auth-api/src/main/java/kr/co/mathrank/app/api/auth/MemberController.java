package kr.co.mathrank.app.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.domain.auth.dto.MemberInfoCompleteCommand;
import kr.co.mathrank.domain.auth.service.MemberRegisterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberRegisterService memberRegisterService;

	@Authorization(openedForAll = true)
	@PutMapping("/api/v1/member/registration")
	public ResponseEntity<Void> completeRegister(
		@RequestBody @Valid final MemberRegistrationCompleteRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final MemberInfoCompleteCommand command = request.toCommand(memberPrincipal.memberId());
		memberRegisterService.completeRegister(command);

		return ResponseEntity.ok().build();
	}
}
