package kr.co.mathrank.app.api.member.read;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.domain.auth.dto.MemberInfoResult;
import kr.co.mathrank.domain.auth.service.MemberQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "계정 API")
@RestController
@RequiredArgsConstructor
public class MemberDetailQueryController {
	private final MemberQueryService memberQueryService;
	private final SchoolClient schoolClient;

	@GetMapping("/api/v1/member/info/my")
	@Operation(summary = "내 계정 정보 조회 API", description = "내 계정의 정보를 조회합니다.")
	@Authorization(openedForAll = true)
	public ResponseEntity<Responses.MemberInfoDetailResponse> getMyInfo(
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final MemberInfoResult result = memberQueryService.getInfo(memberPrincipal.memberId());
		final SchoolInfo schoolInfo = schoolClient.getSchool(RequestType.JSON.getType(), result.schoolCode())
			.orElse(SchoolInfo.none());

		return ResponseEntity.ok(Responses.MemberInfoDetailResponse.from(result, schoolInfo));
	}
}
