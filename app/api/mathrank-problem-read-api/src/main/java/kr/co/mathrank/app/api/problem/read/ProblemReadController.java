package kr.co.mathrank.app.api.problem.read;

import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.domain.problem.read.dto.ProblemReadQuery;
import kr.co.mathrank.domain.problem.read.dto.ProblemResponse;
import kr.co.mathrank.domain.problem.read.service.ProblemReadQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "문제 API")
public class ProblemReadController {
	private final ProblemReadQueryService problemReadQueryService;

	@Operation(summary = "문제 페이지 조회 API", description = "각 필드를 null 로 설정할 시, 해당 필드는 조건에 포함하지 않습니다. +) coursePath 를 통해 조회시, 하위의 과정의 문제들까지 모두 조회됩니다.")
	@Authorization(openedForAll = true)
	@GetMapping(value = "/api/v1/problem")
	public ResponseEntity<PageResult<ProblemResponse>> problems(
		@ParameterObject @ModelAttribute @Valid final ProblemQueryRequest request,
		@LoginInfo final MemberPrincipal loginInfo,
		@NotNull @Range(min = 1, max = 20) Integer pageSize,
		@NotNull @Range(min = 1, max = 1000) Integer pageNumber
	) {
		final ProblemReadQuery query = request.toQuery(loginInfo.memberId());

		return ResponseEntity.ok(problemReadQueryService.queryPages(query, pageSize,
			pageNumber));
	}

	@Operation(summary = "문제 단일 조회 API")
	@GetMapping(value = "/api/v1/problem/{problemId}")
	public ResponseEntity<ProblemResponse> getProblem(
		@PathVariable final Long problemId
	) {
		return ResponseEntity.ok(problemReadQueryService.getProblem(problemId));
	}
}
