package kr.co.mathrank.domain.problem.single.read.service;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelPageResult;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelResult;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemQueryService {
	private final SingleProblemReadModelRepository singleProblemRepository;

	public SingleProblemReadModelPageResult queryPage(
		@NotNull @Valid final SingleProblemReadModelQuery query,
		@NotNull @Range(min = 1, max = 20) final Integer pageSize,
		@NotNull @Range(max = 1000) final Integer pageNumber // 페이지 번호 (1부터 시작). 내부 offset 계산 시 사용됨: offset = (pageNumber - 1) * pageSize
	) {
		final List<SingleProblemReadModel> readModels = singleProblemRepository.queryPage(query, pageSize, pageNumber);
		final Long count = singleProblemRepository.count(query);

		return new SingleProblemReadModelPageResult(
			readModels.stream()
				.map(SingleProblemReadModelResult::from)
				.toList(),
			pageNumber,
			pageSize,
			PageUtil.getNextPages(pageSize, pageNumber, count, readModels.size()
			));
	}
}
