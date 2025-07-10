package kr.co.mathrank.domain.problem.dto;

import jakarta.validation.constraints.NotNull;

public record CourseRegisterCommand(
	@NotNull
	String courseName,
	@NotNull
	String parentPath
) {
}
