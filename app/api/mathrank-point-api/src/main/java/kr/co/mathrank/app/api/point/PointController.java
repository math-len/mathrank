package kr.co.mathrank.app.api.point;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.point.dto.PointProductDeleteCommand;
import kr.co.mathrank.domain.point.dto.PointPurchaseCommand;
import kr.co.mathrank.domain.point.service.PointProductService;
import kr.co.mathrank.domain.point.service.PointPurchaseService;
import lombok.RequiredArgsConstructor;

@Tag(name = "포인트 API")
@RestController
@RequiredArgsConstructor
public class PointController {
	private final PointPurchaseService pointPurchaseService;
	private final PointProductService pointProductService;

	@GetMapping("/api/v1/point/products")
	@Operation(summary = "포인트 상품 목록 조회 API")
	public ResponseEntity<List<Responses.PointProductQueryResponse>> queryAllPointProducts() {
		return ResponseEntity.ok(pointProductService.queryAll().results().stream()
			.map(Responses.PointProductQueryResponse::from)
			.toList());
	}

	@GetMapping("/api/v1/point/product/{productId}")
	@Operation(summary = "포인트 상품 단일 조회 API")
	public ResponseEntity<Responses.PointProductQueryResponse> getSingleDetail(
		@PathVariable final Long productId
	) {
		return ResponseEntity.ok(
			Responses.PointProductQueryResponse.from(pointProductService.querySingle(productId)));
	}

	@PostMapping("/api/v1/point/product")
	@Operation(summary = "포인트 상품 등록 API - 관리자 전용")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Long> register(
		@ModelAttribute @Valid final Requests.PointProductRegisterRequest request
	) {
		final Long productId = pointProductService.save(request.toCommand());
		return ResponseEntity.ok(productId);
	}

	@DeleteMapping("/api/v1/point/product/{productId}")
	@Operation(summary = "포인트 상품 제거 API - 관리자 전용")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> delete(
		@PathVariable final Long productId
	) {
		pointProductService.delete(new PointProductDeleteCommand(productId));

		return ResponseEntity.ok().build();
	}

	@PostMapping("/api/v1/point/product/payment/{paymentId}")
	@Operation(summary = "포인트 충전 API", description = """
		https://developers.portone.io/opi/ko/quick-guide/payment?v=v2
		
		- 이 API를 호출하기 전, PortOne을 통해 결제가 완료되어야 합니다.
		- <결제요청> 단계에서, 반드시 아래 내용을 적용해야 합니다.
			- 1. customData 필드에 아래와 같은 json 형식으로 정보 저장
				{
					"paymentProductId": 상품 id,
					"pointAmount": 구매한 point 양
				}
		  	- 2. customer 필드에 사용자 id값 채우기
		""")
	@Authorization(openedForAll = true)
	public ResponseEntity<Void> charge(
		@PathVariable final String paymentId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final PointPurchaseCommand command = new PointPurchaseCommand(paymentId, memberPrincipal.memberId());
		pointPurchaseService.confirm(command);

		return ResponseEntity.ok().build();
	}
}
