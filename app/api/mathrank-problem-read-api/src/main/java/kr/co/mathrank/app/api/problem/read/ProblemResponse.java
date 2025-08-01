package kr.co.mathrank.app.api.problem.read;

import java.time.LocalDateTime;
import java.util.Set;

import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.client.internal.member.MemberInfo;
import kr.co.mathrank.domain.problem.dto.CourseQueryContainsParentsResult;
import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

public record ProblemResponse(
	String id,
	MemberResponse memberInfo,
	CourseTotalResponse course,
	String problemImage,
	String solutionImage,
	Difficulty difficulty,
	AnswerType type,
	SchoolResponse schoolInfo,
	Set<String> answers,
	LocalDateTime createdAt,
	Integer year,
	String solutionVideoLink,
	String memo
) {
	public static ProblemResponse from(
		final ProblemQueryResult result,
		final MemberInfo memberInfo,
		final SchoolInfo schoolInfo,
		final CourseQueryContainsParentsResult courseResult
	) {
		return new ProblemResponse(
			String.valueOf(result.id()),
			MemberResponse.from(memberInfo),
			CourseTotalResponse.from(courseResult),
			result.imageSource(),
			result.solutionImage(),
			result.difficulty(),
			result.type(),
			SchoolResponse.from(schoolInfo),
			result.answer(),
			result.createdAt(),
			result.year(),
			result.solutionVideoLink(),
			result.memo()
		);
	}
}
