package kr.co.mathrank.app.api.problem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

class Requests {
	record ProblemRegisterRequest(
		@NotNull
		String imageSource,
		@NotBlank
		String solutionImage,
		@NotNull
		AnswerType answerType,
		@NotNull
		String coursePath,
		@NotNull
		Difficulty difficulty,
		@NotNull
		String schoolCode,
		@NotBlank
		String answer,
		Integer year,
		String solutionVideoLink
	) {
		public ProblemRegisterCommand toCommand(final Long memberId) {
			return new ProblemRegisterCommand(memberId,
				imageSource,
				solutionImage,
				answerType,
				coursePath,
				difficulty,
				schoolCode,
				answer,
				year,
				solutionVideoLink);
		}
	}

	record ProblemUpdateRequest (
		@NotNull
		Long problemId,
		@NotNull
		String imageSource,
		@NotBlank
		String solutionImage,
		@NotNull
		AnswerType answerType,
		@NotNull
		String coursePath,
		@NotNull
		Difficulty difficulty,
		@NotNull
		String schoolCode,
		@NotBlank
		String answer,
		Integer year,
		String solutionVideoLink
	) {
		public ProblemUpdateCommand toCommand(final Long memberId) {
			return new ProblemUpdateCommand(
				problemId,
				memberId,
				imageSource,
				answerType,
				difficulty,
				coursePath,
				schoolCode,
				answer);
		}
	}
}
