package kr.co.mathrank.app.api.problem.single.read;

import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelPageResult;
import kr.co.mathrank.domain.problem.single.read.service.SingleProblemQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "개별 문제 API")
@RestController
@RequiredArgsConstructor
public class SingleProblemReadController {
	private final SingleProblemQueryService singleProblemQueryService;

	@GetMapping("/api/v1/problem/single")
	@Operation(summary = "풀이 시도 가능한 개별문제 페이징 조회 API")
	@Authorization(openedForAll = true)
	public ResponseEntity<SingleProblemReadModelPageResult> getSingleProblems(
		@LoginInfo final MemberPrincipal memberPrincipal,
		@ModelAttribute @ParameterObject final SingleProblemQueryRequest query,
		@Range(min = 1, max = 1000) @RequestParam(defaultValue = "1") final Integer pageNumber,
		@Range(min = 1, max = 20) @RequestParam(defaultValue = "1") final Integer pageSize
	) {
		final SingleProblemReadModelPageResult result = singleProblemQueryService.queryPage(
			query.toQuery(),
			memberPrincipal.memberId(),
			pageSize,
			pageNumber
		);
		return ResponseEntity.ok(result);
	}
}
