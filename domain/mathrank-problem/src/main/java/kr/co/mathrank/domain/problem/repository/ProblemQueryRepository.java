package kr.co.mathrank.domain.problem.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.entity.ProblemCourse;

interface ProblemQueryRepository {
	 List<Problem> query(
		 Long memberId,
		 Difficulty difficulty,
		 AnswerType answerType,
		 ProblemCourse problemCourse,
		 Integer pageSize,
		 Integer pageNumber);

	Long count(
		Long memberId,
		Difficulty difficulty,
		AnswerType answerType,
		ProblemCourse problemCourse);
}
