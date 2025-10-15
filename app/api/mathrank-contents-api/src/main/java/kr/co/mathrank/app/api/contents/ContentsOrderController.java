package kr.co.mathrank.app.api.contents;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.domain.contents.dto.ContentOrderCommand;
import kr.co.mathrank.domain.contents.dto.ContentOrderQuery;
import kr.co.mathrank.domain.contents.dto.ContentOrderQueryResult;
import kr.co.mathrank.domain.contents.service.ContentOrderQueryService;
import kr.co.mathrank.domain.contents.service.ContentOrderService;
import lombok.RequiredArgsConstructor;

@Tag(name = "자료실 구매 API")
@RestController
@RequiredArgsConstructor
public class ContentsOrderController {
	private final ContentOrderService contentOrderService;
	private final ContentOrderQueryService contentOrderQueryService;

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

	@GetMapping("/api/v1/content/order/{orderId}")
	@Operation(summary = "주문 상태 조회 API")
	@Authorization(openedForAll = true)
	public ResponseEntity<ContentOrderQueryResult> queryOrderStatus(
		@PathVariable final Long orderId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		return ResponseEntity.ok(contentOrderService.queryOrder(orderId, memberPrincipal.memberId()));
	}

	@GetMapping("/api/v1/content/orders/my")
	@Operation(summary = "본인 주문 내역 조회 API")
	@Authorization(openedForAll = true)
	public ResponseEntity<PageResult<ContentOrderQueryResult>> queryOrders(
		@RequestParam(defaultValue = "1") final Integer pageNumber,
		@RequestParam(defaultValue = "10") final Integer pageSize,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final PageResult<ContentOrderQueryResult> result = contentOrderQueryService.contentOrderPageQuery(
			new ContentOrderQuery(memberPrincipal.memberId()),
			pageSize,
			pageNumber
		);

		return ResponseEntity.ok(result);
	}
}
