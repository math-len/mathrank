package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentPageQuery;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrder;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrderDirection;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentPeriodType;
import kr.co.mathrank.domain.problem.assessment.entity.QAssessment;
import kr.co.mathrank.domain.problem.core.Difficulty;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class AssessmentQueryRepositoryImpl implements AssessmentQueryRepository{
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Assessment> query(AssessmentPageQuery query, int pageSize, int pageNumber, final AssessmentOrder assessmentOrder, final AssessmentOrderDirection direction) {
		final QAssessment qAssessment = QAssessment.assessment;
		return jpaQueryFactory.select(qAssessment)
			.from(qAssessment)
			.where(conditions(query))
			.orderBy(getOrderSpecifier(assessmentOrder, direction))
			.offset((pageNumber - 1) * pageSize)
			.limit(pageSize)
			.fetch();
	}

	@Override
	public Long count(AssessmentPageQuery query) {
		final QAssessment qAssessment = QAssessment.assessment;
		return jpaQueryFactory.select(qAssessment.count())
			.from(qAssessment)
			.where(conditions(query))
			.fetchOne();
	}

	private OrderSpecifier<?> getOrderSpecifier(final AssessmentOrder assessmentOrder, final AssessmentOrderDirection direction) {
		final Order order = switch (direction) {
			case ASC -> Order.ASC;
			case DESC -> Order.DESC;
		};

		final QAssessment qAssessment = QAssessment.assessment;

		return switch (assessmentOrder) {
			case LATEST -> new OrderSpecifier<>(order, qAssessment.createdAt);
			case DISTINCT_USER_COUNT -> new OrderSpecifier<>(order, qAssessment.distinctTriedMemberCount);
		};
	}

	private BooleanExpression[] conditions(final AssessmentPageQuery assessmentQuery) {
		return new BooleanExpression[] {
			difficultyMatches(assessmentQuery.difficulty()),
			nameContains(assessmentQuery.assessmentName()),
			periodTypeMatches(assessmentQuery.periodType())
		};
	}

	private BooleanExpression difficultyMatches(final Difficulty difficulty) {
		if (difficulty == null) {
			return null;
		}

		return QAssessment.assessment.difficulty.eq(difficulty);
	}

	private BooleanExpression nameContains(final String assessmentName) {
		if (assessmentName == null) {
			return null;
		}

		return QAssessment.assessment.assessmentName.contains(assessmentName);
	}

	private BooleanExpression periodTypeMatches(final AssessmentPeriodType assessmentPeriodType) {
		if (assessmentPeriodType == null) {
			return null;
		}

		return QAssessment.assessment.assessmentSubmissionPeriod.periodType.eq(assessmentPeriodType);
	}
}
