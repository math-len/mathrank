package kr.co.mathrank.rank.read;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.client.internal.member.MemberClient;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.domain.rank.service.RankPageQueryService;
import kr.co.mathrank.domain.rank.service.RankQueryService;
import kr.co.mathrank.domain.rank.service.SchoolRankQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "랭크 API")
@RestController
@RequiredArgsConstructor
public class RankReadController {
	private final RankQueryService rankQueryService;
	private final RankPageQueryService rankPageQueryService;
	private final MemberClient memberClient;
	private final SchoolRankQueryService schoolRankQueryService;

	@Operation(summary = "내 랭크 조회 API", description = "사용자의 랭크를 조회합니다. 사용자 문제 풀이 기록에 맞춰 실시간으로 반영됩니다.")
	@GetMapping("/api/v1/rank")
	@Authorization(openedForAll = true)
	public ResponseEntity<RankResponse> queryRank(@RequestParam final Long memberId) {
		return ResponseEntity.ok(RankResponse.from(rankQueryService.getRank(memberId)));
	}

	@Operation(summary = "전체 페이징 조회")
	@GetMapping("/api/v1/rank/all")
	public ResponseEntity<PageResult<RankPageItemResponse>> queryRankPage(
		@RequestParam(defaultValue = "10") final Integer pageSize,
		@RequestParam(defaultValue = "1") final Integer pageNumber
	) {
		return ResponseEntity.ok(rankPageQueryService.queryResultPageResult(pageSize, pageNumber)
			.map(rankItemResult -> RankPageItemResponse.from(rankItemResult,
				memberClient.getMemberInfo(rankItemResult.memberId()))));
	}

	@Operation(summary = "학교 랭크 페이징 조회")
	@GetMapping("/api/v1/rank/schools")
	public ResponseEntity<PageResult<SchoolRankPageResponse>> querySchoolRankPage(
		@RequestParam(defaultValue = "10") final Integer pageSize,
		@RequestParam(defaultValue = "1") final Integer pageNumber
	) {
		return ResponseEntity.ok(schoolRankQueryService.querySchoolRanks(pageSize, pageNumber)
			.map(SchoolRankPageResponse::from));
	}
}
