package kr.co.mathrank.domain.contents.repository;

import java.util.List;

import kr.co.mathrank.domain.contents.dto.ContentReadPageQuery;
import kr.co.mathrank.domain.contents.entity.Content;

interface ContentQueryRepository {
	public abstract List<Content> queryPage(final ContentReadPageQuery query, final int pageSize, final int pageNumber);
	public abstract Long count(final ContentReadPageQuery query);
}
