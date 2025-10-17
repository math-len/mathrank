package kr.co.mathrank.app.api.problem.single.controller;

import jakarta.validation.constraints.NotBlank;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemNameUpdateCommand;

public record SingleProblemNameUpdateRequest(
	@NotBlank
	String singleProblemTitle
) {
	public SingleProblemNameUpdateCommand toCommand(final Long singleProblemId) {
		return new SingleProblemNameUpdateCommand(singleProblemId, singleProblemTitle());
	}
}
