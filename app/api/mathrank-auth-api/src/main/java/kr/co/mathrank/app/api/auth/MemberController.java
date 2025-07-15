package kr.co.mathrank.app.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.mathrank.domain.auth.dto.MemberInfoResult;
import kr.co.mathrank.domain.auth.service.MemberQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberQueryService memberQueryService;

	@Operation(hidden = true)
	@GetMapping("/api/inner/v1/member/info")
	public ResponseEntity<MemberInfoResult> getMemberInfo(
		@RequestParam final Long memberId
	) {
		return ResponseEntity.ok(memberQueryService.getInfo(memberId));
	}
}
