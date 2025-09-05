package kr.co.mathrank.domain.problem.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.dto.ProblemQuery;
import kr.co.mathrank.domain.problem.entity.Problem;

interface ProblemQueryRepository {
	 List<Problem> query(
		 ProblemQuery problemQuery,

		 Integer pageSize,
		 Integer pageNumber);

	Long count(ProblemQuery problemQuery);
}
