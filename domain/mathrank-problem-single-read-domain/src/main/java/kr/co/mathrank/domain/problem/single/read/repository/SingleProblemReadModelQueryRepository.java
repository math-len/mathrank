package kr.co.mathrank.domain.problem.single.read.repository;

import java.util.List;

import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.entity.OrderColumn;
import kr.co.mathrank.domain.problem.single.read.entity.OrderDirection;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;

interface SingleProblemReadModelQueryRepository {
	List<SingleProblemReadModel> queryPage(
		SingleProblemReadModelQuery query,
		int pageSize,
		int pageNumber,
		OrderColumn orderColumn,
		OrderDirection order
	);

	Long count(SingleProblemReadModelQuery query);
}
