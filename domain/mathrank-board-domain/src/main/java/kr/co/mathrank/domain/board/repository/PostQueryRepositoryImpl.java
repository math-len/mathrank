package kr.co.mathrank.domain.board.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.mathrank.domain.board.dto.PostPageQuery;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.QPost;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class PostQueryRepositoryImpl implements PostQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Post> findAllByPage(PostPageQuery postQuery, int pageSize, int pageNumber) {
		final QPost post = QPost.post;

		return queryFactory.select(post)
			.from(post)
			.where(
				conditions(postQuery)
			)
			.offset(pageSize * (pageNumber - 1))
			.limit(pageSize)
			.orderBy(new OrderSpecifier<>(Order.DESC, post.createdAt))
			.fetch();
	}

	@Override
	public Long count(PostPageQuery postQuery) {
		final QPost post = QPost.post;

		return queryFactory.query()
			.select(post.count())
			.from(post)
			.where(
				conditions(postQuery)
			)
			.fetchOne();
	}

	private Predicate[] conditions(PostPageQuery postQuery) {
		return new Predicate[] {
			matchPostId(postQuery.postId()),
			matchAssessmentId(postQuery.assessmentId()),
			matchContestId(postQuery.contestId()),
			containsTitle(postQuery.title()),
			containsMemberNickName(postQuery.nickName()),
			matchMemberId(postQuery.memberId())
		};
	}

	private BooleanExpression matchMemberId(final Long memberId) {
		if (memberId == null) {
			return null;
		}

		return QPost.post.memberId.eq(memberId);
	}

	private BooleanExpression matchPostId(final Long postId) {
		if (postId == null) {
			return null;
		}

		return QPost.post.id.eq(postId);
	}

	private BooleanExpression containsMemberNickName(final String nickName) {
		if (nickName == null) {
			return null;
		}

		return QPost.post.memberNickName.contains(nickName);
	}

	private BooleanExpression matchAssessmentId(final Long assessmentId) {
		if (assessmentId == null) {
			return null;
		}

		return QPost.post.assessmentId.eq(assessmentId);
	}

	private BooleanExpression matchContestId(final Long contestId) {
		if (contestId == null) {
			return null;
		}
		return QPost.post.contestId.eq(contestId);
	}

	private BooleanExpression containsTitle(final String postTitle) {
		if (postTitle == null) {
			return null;
		}

		return QPost.post.title.contains(postTitle);
	}
}
