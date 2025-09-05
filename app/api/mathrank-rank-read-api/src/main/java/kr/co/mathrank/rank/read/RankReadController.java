package kr.co.mathrank.rank.read;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.domain.rank.service.RankQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "랭크 API")
@RestController
@RequiredArgsConstructor
public class RankReadController {
	private final RankQueryService rankQueryService;

	@Operation(summary = "랭크 조회 API", description = "사용자의 랭크를 조회합니다. 사용자 문제 풀이 기록에 맞춰 실시간으로 반영됩니다.")
	@GetMapping("/api/v1/rank")
	@Authorization(openedForAll = true)
	public ResponseEntity<RankResponse> queryRank(@RequestParam final Long memberId) {
		return ResponseEntity.ok(RankResponse.from(rankQueryService.getRank(memberId)));
	}
}
