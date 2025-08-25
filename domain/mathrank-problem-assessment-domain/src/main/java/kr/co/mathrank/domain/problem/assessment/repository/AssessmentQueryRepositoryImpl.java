package kr.co.mathrank.domain.problem.assessment.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQuery;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.QAssessment;
import kr.co.mathrank.domain.problem.core.Difficulty;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class AssessmentQueryRepositoryImpl implements AssessmentQueryRepository{
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Assessment> query(AssessmentQuery query, int pageSize, int pageNumber) {
		final QAssessment qAssessment = QAssessment.assessment;
		return jpaQueryFactory.select(qAssessment)
			.from(qAssessment)
			.where(conditions(query))
			.orderBy(qAssessment.distinctTriedMemberCount.desc())
			.offset((pageNumber - 1) * pageSize)
			.limit(pageSize)
			.fetch();
	}

	@Override
	public Long count(AssessmentQuery query) {
		final QAssessment qAssessment = QAssessment.assessment;
		return jpaQueryFactory.select(qAssessment.count())
			.from(qAssessment)
			.where(conditions(query))
			.fetchOne();
	}

	private BooleanExpression[] conditions(final AssessmentQuery assessmentQuery) {
		return new BooleanExpression[] {
			difficultyMatches(assessmentQuery.difficulty()),
			nameContains(assessmentQuery.assessmentName())
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
}
