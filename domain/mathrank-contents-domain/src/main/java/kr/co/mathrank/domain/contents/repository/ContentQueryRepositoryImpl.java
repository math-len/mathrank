package kr.co.mathrank.domain.contents.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.contents.dto.ContentReadPageQuery;
import kr.co.mathrank.domain.contents.entity.Content;
import kr.co.mathrank.domain.contents.entity.ContentType;
import kr.co.mathrank.domain.contents.entity.QContent;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ContentQueryRepositoryImpl implements ContentQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Content> queryPage(@NotNull @Valid final ContentReadPageQuery query, final int pageSize, final int pageNumber) {
		final QContent content = QContent.content;

		return queryFactory.select(content)
			.from(content)
			.where(
				whereConditions(query)
			)
			.orderBy(new OrderSpecifier<>(Order.DESC, content.createdAt))
			.offset(pageNumber * pageSize)
			.limit(pageSize)
			.fetch();
	}

	@Override
	public Long count(@NotNull @Valid final ContentReadPageQuery query) {
		final QContent content = QContent.content;

		return queryFactory.select(content.count())
			.from(content)
			.where(
				whereConditions(query)
			)
			.fetchOne();
	}

	public BooleanExpression[] whereConditions(final ContentReadPageQuery query) {
		return new BooleanExpression[] {
			containsTitle(query.title()),
			matchContentType(query.contentType())
		};
	}

	public BooleanExpression containsTitle(final String title) {
		if (title == null || title.isBlank()) {
			return null;
		}

		return QContent.content.title.contains(title);
	}

	public BooleanExpression matchContentType(final ContentType contentType) {
		if (contentType == null) {
			return null;
		}

		return QContent.content.contentType.eq(contentType);
	}
}
