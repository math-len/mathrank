package kr.co.mathrank.app.api.problem;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;

class Requests {
	record ProblemRegisterRequest(
		@NotNull
		String problemImage,
		/*
			문제 등록시, 풀이 이미지 필수 아니도록 수정
		*/
		// @NotBlank
		String solutionImage,
		@NotNull
		AnswerType answerType,
		@NotNull
		String coursePath,
		@NotNull
		Difficulty difficulty,
		@NotNull
		PastProblem pastProblem,
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
				pastProblem,
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
		String problemImage,
		/*
			문제 수정시, 풀이 이미지 필수 아니도록 수정
		*/
		// @NotBlank
		String solutionImage,
		@NotNull
		AnswerType answerType,
		@NotNull
		String coursePath,
		@NotNull
		Difficulty difficulty,
		@NotNull
		PastProblem pastProblem,
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
				problemImage,
				solutionImage,
				answerType,
				difficulty,
				pastProblem,
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
