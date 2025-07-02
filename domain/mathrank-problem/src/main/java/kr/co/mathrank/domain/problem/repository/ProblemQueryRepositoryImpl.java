package kr.co.mathrank.domain.problem.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.entity.ProblemCourse;
import kr.co.mathrank.domain.problem.entity.QProblem;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ProblemQueryRepositoryImpl implements ProblemQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Problem> query(Long memberId, Difficulty difficulty, AnswerType answerType, ProblemCourse problemCourse, Integer pageSize, Integer pageNumber) {
		final QProblem problem = QProblem.problem;

		return queryFactory.query()
			.select(problem)
			.from(problem)
			.where(
				memberIdEq(memberId),
				difficultyEq(difficulty),
				answerTypeEq(answerType),
				problemCourseEq(problemCourse)
			)
			.offset((pageNumber - 1) * pageSize)
			.limit(pageSize)
			.fetch();
	}

	@Override
	public Long count(Long memberId, Difficulty difficulty, AnswerType answerType, ProblemCourse problemCourse) {
		final QProblem problem = QProblem.problem;

		return queryFactory.query()
			.select(problem.count())
			.from(problem)
			.where(
				memberIdEq(memberId),
				difficultyEq(difficulty),
				answerTypeEq(answerType),
				problemCourseEq(problemCourse)
			)
			.fetchOne();
	}

	private BooleanExpression memberIdEq(final Long memberId) {
		if (memberId == null) {
			return null;
		}
		return QProblem.problem.memberId.eq(memberId);
	}

	private BooleanExpression difficultyEq(final Difficulty difficulty) {
		if (difficulty == null) {
			return null;
		}
		return QProblem.problem.difficulty.eq(difficulty);
	}

	private BooleanExpression answerTypeEq(final AnswerType answerType) {
		if (answerType == null) {
			return null;
		}
		return QProblem.problem.type.eq(answerType);
	}

	private BooleanExpression problemCourseEq(final ProblemCourse problemCourse) {
		if (problemCourse == null) {
			return null;
		}
		return QProblem.problem.problemCourse.eq(problemCourse);
	}
}
