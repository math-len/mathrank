package kr.co.mathrank.domain.contents.repository;

import java.util.List;

import kr.co.mathrank.domain.contents.dto.ContentOrderQuery;
import kr.co.mathrank.domain.contents.entity.ContentOrder;

public interface ContentOrderQueryRepository {
	/**
	 *
	 * @param query
	 * @param pageSize
	 * @param pageNumber 0-based
	 * @return
	 */
	public abstract List<ContentOrder> query(
		final ContentOrderQuery query,
		final int pageSize,
		final int pageNumber
	);

	public abstract Long count(
		final ContentOrderQuery query
	);
}
