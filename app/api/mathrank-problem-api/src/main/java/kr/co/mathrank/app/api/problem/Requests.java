package kr.co.mathrank.app.api.problem;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

class Requests {
	record ProblemRegisterRequest(
		@NotNull
		String problemImage,
		@NotBlank
		String solutionImage,
		@NotNull
		AnswerType answerType,
		@NotNull
		String coursePath,
		@NotNull
		Difficulty difficulty,
		String schoolCode,
		@Size(min = 1, max = 100)
		Set<String> answers,
		Integer year,
		String solutionVideoLink,
		String memo
	) {
		public ProblemRegisterCommand toCommand(final Long memberId) {
			return new ProblemRegisterCommand(memberId,
				problemImage,
				solutionImage,
				answerType,
				coursePath,
				difficulty,
				schoolCode,
				answers,
				year,
				solutionVideoLink,
				memo);
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
		String schoolCode,
		@Size(min = 1, max = 100)
		Set<String> answers,
		Integer year,
		String solutionVideoLink,
		String memo
	) {
		public ProblemUpdateCommand toCommand(final Long memberId) {
			return new ProblemUpdateCommand(
				problemId,
				memberId,
				imageSource,
				solutionImage,
				answerType,
				difficulty,
				coursePath,
				schoolCode,
				answers,
				year,
				solutionVideoLink,
				memo
			);
		}
	}
}
