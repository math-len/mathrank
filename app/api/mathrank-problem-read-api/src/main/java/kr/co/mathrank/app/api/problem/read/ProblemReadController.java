package kr.co.mathrank.app.api.problem.read;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.client.internal.member.MemberClient;
import kr.co.mathrank.client.internal.member.MemberInfo;
import kr.co.mathrank.domain.problem.dto.CourseQueryContainsParentsResult;
import kr.co.mathrank.domain.problem.dto.ProblemQueryCommand;
import kr.co.mathrank.domain.problem.dto.ProblemQueryPageResult;
import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.service.CourseQueryService;
import kr.co.mathrank.domain.problem.service.ProblemQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "문제 API")
public class ProblemReadController {
	private final ProblemQueryService problemQueryService;
	private final CourseQueryService courseQueryService;
	private final MemberClient memberClient;
	private final SchoolClient schoolClient;

	@Operation(summary = "문제 페이지 조회 API", description = "각 필드를 null 로 설정할 시, 해당 필드는 조건에 포함하지 않습니다. +) coursePath 를 통해 조회시, 하위의 과정의 문제들까지 모두 조회됩니다.")
	@Authorization(openedForAll = true)
	@GetMapping(value = "/api/v1/problem")
	public ResponseEntity<ProblemPageResponse> problems(
		@ParameterObject @ModelAttribute @Valid final ProblemQueryRequest request,
		@LoginInfo final MemberPrincipal loginInfo
	) {
		final ProblemQueryCommand command = request.toCommand(loginInfo.memberId());
		final ProblemQueryPageResult pageQueryResult = problemQueryService.query(command);

		final ProblemPageResponse containsUserName = ProblemPageResponse.from(
			withOtherDatas(pageQueryResult.queryResults()), pageQueryResult);

		return ResponseEntity.ok(containsUserName);
	}

	@Operation(summary = "문제 단일 조회 API")
	@GetMapping(value = "/api/v1/problem/single")
	public ResponseEntity<ProblemResponse> getProblem(
		@RequestParam final Long problemId
	) {
		final ProblemQueryResult result = problemQueryService.getSingle(problemId);
		final ProblemResponse response = toResponse(result);

		return ResponseEntity.ok(response);
	}

	private List<ProblemResponse> withOtherDatas(final List<ProblemQueryResult> queryResults) {
		return queryResults.stream()
			.map(this::toResponse)
			.toList();
	}

	private ProblemResponse toResponse(final ProblemQueryResult problem) {
		final MemberInfo info = memberClient.getMemberInfo(problem.memberId());
		final SchoolInfo schoolInfo = schoolClient.getSchool(RequestType.JSON.getType(), problem.schoolCode())
			.orElse(SchoolInfo.none());
		final CourseQueryContainsParentsResult result = courseQueryService.queryParents(problem.path());
		return ProblemResponse.from(problem, info, schoolInfo, result);
	}
}
