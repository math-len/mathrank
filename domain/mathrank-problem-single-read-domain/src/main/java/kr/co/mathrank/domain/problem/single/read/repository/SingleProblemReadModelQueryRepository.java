package kr.co.mathrank.domain.problem.single.read.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;

interface SingleProblemReadModelQueryRepository {
	List<SingleProblemReadModel> queryPage(SingleProblemReadModelQuery query, int pageSize, int pageNumber);
	Long count(SingleProblemReadModelQuery query);
}
