package kr.co.mathrank.domain.contents.repository;

import java.util.List;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.mathrank.domain.contents.dto.ContentOrderQuery;
import kr.co.mathrank.domain.contents.entity.ContentOrder;
import kr.co.mathrank.domain.contents.entity.QContentOrder;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ContentOrderQueryRepositoryImpl implements ContentOrderQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ContentOrder> query(ContentOrderQuery query, int pageSize, int pageNumber) {
		final QContentOrder contentOrder = QContentOrder.contentOrder;

		return queryFactory.select(contentOrder)
			.from(contentOrder)
			.where(
				whereConditions(query)
			)
			.leftJoin(contentOrder.content).fetchJoin()
			.limit(pageSize)
			.offset(pageNumber * pageSize)
			.orderBy(new OrderSpecifier<>(Order.DESC, contentOrder.createdAt))
			.fetch();
	}

	@Override
	public Long count(ContentOrderQuery query) {
		final QContentOrder contentOrder = QContentOrder.contentOrder;

		return queryFactory.select(contentOrder.count())
			.from(contentOrder)
			.where(
				whereConditions(query)
			)
			.fetchOne();
	}

	private BooleanExpression[] whereConditions(ContentOrderQuery query) {
		return new BooleanExpression[] {
			matchMemberId(query.memberId())
		};
	}

	private BooleanExpression matchMemberId(final Long memberId) {
		if (memberId == null) {
			return null;
		}

		return QContentOrder.contentOrder.userId.eq(memberId);
	}
}
