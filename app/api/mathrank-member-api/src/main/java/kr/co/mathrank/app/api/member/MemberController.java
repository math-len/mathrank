package kr.co.mathrank.app.api.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.domain.auth.dto.MemberUpdateCommand;
import kr.co.mathrank.domain.auth.service.MemberUpdateService;
import lombok.RequiredArgsConstructor;

@Tag(name = "계정 API")
@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberUpdateService memberUpdateService;

	@PutMapping("/api/v1/member/info/my")
	@Operation(summary = "내 계정 정보 업데이트 API", description = "내 계정의 정보를 주어진 페이로드로 업데이트 합니다.")
	@Authorization(openedForAll = true)
	public ResponseEntity<Void> updateMyInfo(
		@LoginInfo final MemberPrincipal memberPrincipal,
		@RequestBody final Requests.MemberUpdateRequest request
	) {
		final MemberUpdateCommand command = request.toCommand(memberPrincipal.memberId());
		memberUpdateService.update(command);

		return ResponseEntity.ok().build();
	}
}
