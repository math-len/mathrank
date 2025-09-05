package kr.co.mathrank.rank.read;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.domain.rank.service.RankQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RankReadController {
	private final RankQueryService rankQueryService;

	@GetMapping("/api/v1/rank")
	@Authorization(openedForAll = true)
	public ResponseEntity<RankResponse> queryRank(@RequestParam final Long memberId) {
		return ResponseEntity.ok(RankResponse.from(rankQueryService.getRank(memberId)));
	}
}
