package kr.co.mathrank.domain.problem.read.service;

import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.client.internal.course.CourseClient;
import kr.co.mathrank.client.internal.course.CourseQueryContainsParentsResult;
import kr.co.mathrank.client.internal.member.MemberClient;
import kr.co.mathrank.client.internal.member.MemberInfo;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.read.dto.ProblemReadQuery;
import kr.co.mathrank.domain.problem.read.dto.ProblemResponse;
import kr.co.mathrank.domain.problem.service.ProblemQueryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemReadQueryService {
	private final ProblemQueryService problemQueryService;
	private final MemberClient memberClient;
	private final CourseClient courseClient;
	private final SchoolClient schoolClient;

	public PageResult<ProblemResponse> queryPages(
		@Valid final ProblemReadQuery query,
		@NotNull @Range(min = 1, max = 20) Integer pageSize,
		@NotNull @Range(min = 1, max = 1000) Integer pageNumber
	) {
		final PageResult<ProblemQueryResult> pageQueryResult = problemQueryService.query(query.toQuery(), pageSize, pageNumber);

		return pageQueryResult.map(this::mapInfos);
	}

	public ProblemResponse getProblem(final Long problemId) {
		final ProblemQueryResult result = problemQueryService.getSingle(problemId);

		return mapInfos(result);
	}

	private ProblemResponse mapInfos(final ProblemQueryResult problem) {
		final MemberInfo info = memberClient.getMemberInfo(problem.memberId());
		final SchoolInfo schoolInfo = schoolClient.getSchool(RequestType.JSON.getType(), problem.schoolCode())
			.orElse(SchoolInfo.none());
		final CourseQueryContainsParentsResult result = courseClient.getParentCourses(problem.path());
		return ProblemResponse.from(problem, info, schoolInfo, result);
	}
}
