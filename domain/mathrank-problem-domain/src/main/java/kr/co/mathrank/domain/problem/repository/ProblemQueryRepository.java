package kr.co.mathrank.domain.problem.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.entity.Problem;

interface ProblemQueryRepository {
	 List<Problem> query(
		 Long memberId,
		 Long problemId,
		 Difficulty difficultyMinInclude,
		 Difficulty difficultyMaxInclude,
		 AnswerType answerType,
		 String path,
		 Integer pageSize,
		 Integer pageNumber,
		 Boolean solutionVideoExist,
		 Integer year);

	Long count(
		Long memberId,
		Long problemId,
		Difficulty difficultyMinInclude,
		Difficulty difficultyMaxInclude,
		String path,
		AnswerType answerType,
		Boolean solutionVideoExist,
		Integer year);
}
