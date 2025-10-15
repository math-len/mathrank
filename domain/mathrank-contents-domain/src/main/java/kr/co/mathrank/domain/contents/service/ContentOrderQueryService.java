package kr.co.mathrank.domain.contents.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.contents.dto.ContentOrderQuery;
import kr.co.mathrank.domain.contents.dto.ContentOrderQueryResult;
import kr.co.mathrank.domain.contents.entity.ContentOrder;
import kr.co.mathrank.domain.contents.repository.ContentOrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class ContentOrderQueryService {
	private final ContentOrderRepository contentOrderRepository;

	public PageResult<ContentOrderQueryResult> contentOrderPageQuery(
		@NotNull final ContentOrderQuery query,
		@NotNull final int pageSize,
		@NotNull final int pageNumber
	) {
		final List<ContentOrder> contentOrders = contentOrderRepository.query(query, pageSize, pageNumber - 1);
		final Long totalCount = contentOrderRepository.count(query);

		return PageResult.of(
			contentOrders,
			pageNumber,
			pageSize,
			PageUtil.getNextPages(pageSize, pageNumber, totalCount, contentOrders.size())
		).map(ContentOrderQueryResult::from);
	}
}
