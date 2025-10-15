package kr.co.mathrank.domain.contents.service;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.contents.ContentsCacheConfiguration;
import kr.co.mathrank.domain.contents.dto.ContentOrderQuery;
import kr.co.mathrank.domain.contents.dto.ContentOrderQueryResult;
import kr.co.mathrank.domain.contents.entity.ContentOrder;
import kr.co.mathrank.domain.contents.repository.ContentOrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
@CacheConfig(cacheNames = ContentsCacheConfiguration.CONTENT_ORDER_PAGE_CACHE_NAME)
public class ContentOrderQueryService {
	private final ContentOrderRepository contentOrderRepository;

	@Cacheable(
		key = "'userId::' + #query.memberId() +'::pageSize::' + #pageSize + '::pageNumber::' + #pageNumber",
		condition = "#pageNumber < 3"
	)
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
