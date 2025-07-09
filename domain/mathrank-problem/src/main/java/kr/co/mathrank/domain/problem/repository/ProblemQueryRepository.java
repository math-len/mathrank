package kr.co.mathrank.domain.problem.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Problem;

interface ProblemQueryRepository {
	 List<Problem> query(
		 Long memberId,
		 Difficulty difficulty,
		 AnswerType answerType,
		 String path,
		 Integer pageSize,
		 Integer pageNumber,
		 Boolean solutionVideoExist,
		 Integer year);

	Long count(
		Long memberId,
		Difficulty difficulty,
		String path,
		AnswerType answerType,
		Boolean solutionVideoExist,
		Integer year);
}
