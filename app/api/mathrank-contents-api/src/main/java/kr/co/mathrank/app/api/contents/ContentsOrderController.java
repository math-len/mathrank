package kr.co.mathrank.app.api.contents;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.domain.contents.dto.ContentOrderCommand;
import kr.co.mathrank.domain.contents.service.ContentOrderService;
import lombok.RequiredArgsConstructor;

@Tag(name = "자료실 구매 API")
@RestController
@RequiredArgsConstructor
public class ContentsOrderController {
	private final ContentOrderService contentOrderService;

	@PostMapping("/api/v1/content/{contentId}")
	@Operation(summary = "자료 주문 API", description = "주문 ID를 반환합니다.")
	@Authorization(openedForAll = true)
	public ResponseEntity<Long> createOrder(
		@PathVariable final Long contentId,
		@RequestParam final String idempotencyKey,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final ContentOrderCommand command = new ContentOrderCommand(
			memberPrincipal.memberId(),
			contentId,
			idempotencyKey
		);

		final Long orderId = contentOrderService.purchase(command);

		return ResponseEntity.ok(orderId);
	}

}
