package kr.co.mathrank.domain.problem.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.dto.ProblemQuery;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.entity.QProblem;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class ProblemQueryRepositoryImpl implements ProblemQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Problem> query(ProblemQuery problemQuery, Integer pageSize, Integer pageNumber) {
		final QProblem problem = QProblem.problem;

		final List<Problem> problems = queryFactory.query()
			.select(problem)
			.from(problem)
			.where(
				conditions(problemQuery)
			)
			.offset((pageNumber - 1) * pageSize)
			.limit(pageSize)
			.fetch();

		// batchsize를 통해 trigger
		// 없을땐 수행하지 않도록.
		if (problems.isEmpty()) {
			return problems;
		}
		problems.getFirst().getAnswers();
		return problems;
	}

	@Override
	public Long count(ProblemQuery problemQuery) {
		final QProblem problem = QProblem.problem;

		return queryFactory.query()
			.select(problem.count())
			.from(problem)
			.where(
				conditions(problemQuery)
			)
			.fetchOne();
	}

	private BooleanExpression[] conditions(final ProblemQuery problemQuery) {
		return new BooleanExpression[] {
			memberIdEq(problemQuery.memberId()),
			problemIdEq(problemQuery.problemId()),
			difficultyIn(problemQuery.difficultyMinInclude(), problemQuery.difficultyMaxInclude()),
			answerTypeEq(problemQuery.answerType()),
			problemCourseStartsWith(problemQuery.path()),
			solutionVideoLinkExist(problemQuery.solutionVideoExist()),
			yearMatch(problemQuery.year()),
			locationMatch(problemQuery.location())
		};
	}

	private BooleanExpression memberIdEq(final Long memberId) {
		if (memberId == null) {
			return null;
		}
		return QProblem.problem.memberId.eq(memberId);
	}

	private BooleanExpression problemIdEq(final Long problemId) {
		if (problemId == null) {
			return null;
		}
		return QProblem.problem.id.eq(problemId);
	}

	private BooleanExpression difficultyIn(final Difficulty minInclude, final Difficulty maxInclude) {
		if (minInclude == null && maxInclude == null) {
			return null;
		}
		return QProblem.problem.difficulty.between(minInclude, maxInclude);
	}

	private BooleanExpression answerTypeEq(final AnswerType answerType) {
		if (answerType == null) {
			return null;
		}
		return QProblem.problem.type.eq(answerType);
	}

	private BooleanExpression problemCourseStartsWith(final String path) {
		if (path == null) {
			return null;
		}
		return QProblem.problem.coursePath.startsWith(path);
	}

	private BooleanExpression solutionVideoLinkExist(final Boolean solutionVideoLinkExist) {
		if (solutionVideoLinkExist == null) {
			return null;
		}
		if (solutionVideoLinkExist) {
			return QProblem.problem.solutionVideoLink.isNotNull();
		}
		return QProblem.problem.solutionVideoLink.isNull();
	}

	private BooleanExpression yearMatch(final Integer year) {
		if (year == null) {
			return null;
		}
		return QProblem.problem.years.eq(year);
	}

	private BooleanExpression locationMatch(final String location) {
		if (location == null) {
			return null;
		}
		return QProblem.problem.location.contains(location);
	}
}
