package kr.co.mathrank.domain.problem.single.read.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.entity.QSingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class SingleProblemReadModelQueryRepositoryImpl implements SingleProblemReadModelQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<SingleProblemReadModel> queryPage(SingleProblemReadModelQuery query, int pageSize,
		int pageNumber) {
		final QSingleProblemReadModel model = QSingleProblemReadModel.singleProblemReadModel;

		return queryFactory.select(model)
			.from(model)
			.where(
				singleProblemIdMatch(query.singleProblemId()),
				diffcultyIn(query.difficultyMinInclude(), query.difficultyMaxInclude()),
				coursePathMatch(query.coursePath()),
				answerTypeEqual(query.answerType()),
				accuracyIn(query.accuracyMinInclude(), query.accuracyMaxInclude()),
				totalAttemptCountIn(query.totalAttemptCountMinInclude(), query.totalAttemptCountMaxInclude())
			)
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
				singleProblemIdMatch(query.singleProblemId()),
				diffcultyIn(query.difficultyMinInclude(), query.difficultyMaxInclude()),
				coursePathMatch(query.coursePath()),
				answerTypeEqual(query.answerType()),
				accuracyIn(query.accuracyMinInclude(), query.accuracyMaxInclude()),
				totalAttemptCountIn(query.totalAttemptCountMinInclude(), query.totalAttemptCountMaxInclude())
			)
			.fetchOne();
	}

	private BooleanExpression singleProblemIdMatch(final Long singleProblemId) {
		if (singleProblemId == null) {
			return null;
		}

		return QSingleProblemReadModel.singleProblemReadModel.id.eq(singleProblemId);
	}

	private BooleanExpression diffcultyIn(final Difficulty difficultyMinInclude, final Difficulty difficultyMaxInclude) {
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
