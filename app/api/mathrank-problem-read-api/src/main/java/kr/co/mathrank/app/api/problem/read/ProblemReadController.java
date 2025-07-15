package kr.co.mathrank.app.api.problem.read;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.client.internal.member.MemberClient;
import kr.co.mathrank.client.internal.member.MemberInfo;
import kr.co.mathrank.domain.problem.dto.ProblemQueryCommand;
import kr.co.mathrank.domain.problem.dto.ProblemQueryPageResult;
import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.service.ProblemQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "problem")
public class ProblemReadController {
	private final ProblemQueryService problemQueryService;
	private final MemberClient memberClient;
	private final SchoolClient schoolClient;

	@GetMapping(value = "/api/v1/problem")
	public ResponseEntity<ProblemPageResponse> problems(
		@ParameterObject @ModelAttribute @Valid final ProblemQueryCommand command
	) {
		final ProblemQueryPageResult pageQueryResult = problemQueryService.query(command);

		final ProblemPageResponse containsUserName = ProblemPageResponse.from(
			withUserName(pageQueryResult.queryResults()), pageQueryResult);

		return ResponseEntity.ok(containsUserName);
	}

	private List<ProblemResponse> withUserName(final List<ProblemQueryResult> queryResults) {
		return queryResults.stream()
			.map(problem -> {
				final MemberInfo info = memberClient.getMemberInfo(problem.memberId());
				final SchoolInfo schoolInfo = schoolClient.getSchool(RequestType.JSON.getType(), problem.schoolCode())
					.orElse(SchoolInfo.none());
				return ProblemResponse.from(problem, info, schoolInfo);
			})
			.toList();
	}
}
