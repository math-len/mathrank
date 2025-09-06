package kr.co.mathrank.domain.problem.single.read.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.entity.OrderColumn;
import kr.co.mathrank.domain.problem.single.read.entity.OrderDirection;
import kr.co.mathrank.domain.problem.single.read.entity.QSingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class SingleProblemReadModelQueryRepositoryImpl implements SingleProblemReadModelQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<SingleProblemReadModel> queryPage(SingleProblemReadModelQuery query, int pageSize,
		int pageNumber, OrderColumn orderColumn, OrderDirection order) {
		final QSingleProblemReadModel model = QSingleProblemReadModel.singleProblemReadModel;

		return queryFactory.select(model)
			.from(model)
			.where(
				createWherePredicates(query)
			)
			.orderBy(orderSpecifier(order, orderColumn))
			.offset((pageNumber - 1) * pageSize)
			.limit(pageSize)
			.fetch();
	}

	@Override
	public Long count(SingleProblemReadModelQuery query) {
		final QSingleProblemReadModel model = QSingleProblemReadModel.singleProblemReadModel;
		return queryFactory.select(model.count())
			.from(model)
			.where(
				createWherePredicates(query)
			)
			.fetchOne();
	}

	private OrderSpecifier<?> orderSpecifier(OrderDirection direction, OrderColumn orderColumn) {
		final Order order = switch (direction) {
			case ASC -> Order.ASC;
			case DESC -> Order.DESC;
			case null -> Order.DESC;
		};

		final QSingleProblemReadModel model = QSingleProblemReadModel.singleProblemReadModel;

		return switch (orderColumn) {
			case DATE -> new OrderSpecifier<>(order, model.createdAt);
			case TOTAL_TRY_COUNT -> new OrderSpecifier<>(order, model.totalAttemptedCount);
			case null -> new OrderSpecifier<>(order, model.createdAt);
		};
	}

	private BooleanExpression[] createWherePredicates(SingleProblemReadModelQuery query) {
		return new BooleanExpression[] {
			singleProblemIdMatch(query.singleProblemId()),
			difficultyIn(query.difficultyMinInclude(), query.difficultyMaxInclude()),
			coursePathMatch(query.coursePath()),
			accuracyIn(query.accuracyMinInclude(), query.accuracyMaxInclude()),
			totalAttemptCountIn(query.totalAttemptCountMinInclude(), query.totalAttemptCountMaxInclude()),
			answerTypeEqual(query.answerType()),
			containsSingleProblemName(query.singleProblemName()),
			startsWithLocation(query.location()),
			schoolCodeMatch(query.schoolCode())
		};
	}

	private BooleanExpression schoolCodeMatch(final String schoolCode) {
		if (schoolCode == null || schoolCode.isBlank()) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.schoolCode.eq(schoolCode);
	}

	private BooleanExpression startsWithLocation(final String location) {
		if (location == null || location.isBlank()) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.location.contains(location);
	}

	private BooleanExpression containsSingleProblemName(final String singleProblemName) {
		if (singleProblemName == null) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.singleProblemName.contains(singleProblemName);
	}

	private BooleanExpression singleProblemIdMatch(final Long singleProblemId) {
		if (singleProblemId == null) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.id.eq(singleProblemId);
	}

	private BooleanExpression difficultyIn(final Difficulty difficultyMinInclude, final Difficulty difficultyMaxInclude) {
		if (difficultyMinInclude == null && difficultyMaxInclude == null) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.difficulty.between(difficultyMinInclude, difficultyMaxInclude);
	}

	private BooleanExpression coursePathMatch(final String coursePath) {
		if (coursePath == null) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.coursePath.startsWith(coursePath);
	}

	private BooleanExpression accuracyIn(final Integer accuracyMinInclude, final Integer accuracyMaxInclude) {
		if (accuracyMinInclude == null && accuracyMaxInclude == null) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.accuracy.between(accuracyMinInclude, accuracyMaxInclude);
	}

	private BooleanExpression totalAttemptCountIn(final Long attemptCountMinInclude, final Long attemptCountMaxInclude) {
		if (attemptCountMinInclude == null && attemptCountMaxInclude == null) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.totalAttemptedCount.between(attemptCountMinInclude, attemptCountMaxInclude);
	}

	private BooleanExpression answerTypeEqual(final AnswerType answerType) {
		if (answerType == null) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.answerType.eq(answerType);
	}
}
