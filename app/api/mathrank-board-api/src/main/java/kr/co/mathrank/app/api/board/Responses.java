package kr.co.mathrank.app.api.board;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.client.internal.member.MemberInfo;
import kr.co.mathrank.domain.board.dto.CommentDetailResult;
import kr.co.mathrank.domain.board.dto.PostDetailQueryResult;
import kr.co.mathrank.domain.board.dto.PostPageQueryResult;
import kr.co.mathrank.domain.board.entity.PostType;

public class Responses {
	record PostDetailResponse(
		String postId,
		MemberResponse memberInfo,
		PostType postType,
		String title,
		String content,
		List<CommentDetailResponse> comments,
		String singleProblemId,
		String assessmentId,
		String contestId,
		LocalDateTime createdAt
	) {
		public static PostDetailResponse from(final PostDetailQueryResult postResult, final List<CommentDetailResponse> comments) {
			return new PostDetailResponse(
				String.valueOf(postResult.postId()),
				MemberResponse.from(postResult.memberId(), postResult.memberNickName()),
				postResult.postType(),
				postResult.title(),
				postResult.content(),
				comments,
				String.valueOf(postResult.singleProblemId()),
				String.valueOf(postResult.assessmentId()),
				String.valueOf(postResult.contestId()),
				postResult.createdAt()
			);
		}
	}

	record CommentDetailResponse(
		String commentId,
		MemberResponse memberInfo,
		String content,
		LocalDateTime createdAt
	) {
		public static CommentDetailResponse from(final CommentDetailResult commentDetailResult, final MemberInfo memberInfo) {
			return new CommentDetailResponse(
				String.valueOf(commentDetailResult.commentId()),
				MemberResponse.from(memberInfo),
				commentDetailResult.content(),
				commentDetailResult.createdAt()
			);
		}
	}

	record MemberResponse(
		String memberId,
		String nickName
	) {
		public static MemberResponse from(final MemberInfo memberInfo) {
			return new MemberResponse(
				String.valueOf(memberInfo.memberId()),
				memberInfo.memberName()
			);
		}

		// Post의 경우, 닉네임 기반 조회라 client를 통한 닉네임 조회 필요 없음
		public static MemberResponse from(final Long memberId, final String nickName) {
			return new MemberResponse(
				String.valueOf(memberId),
				nickName
			);
		}
	}

	record PostPageQueryResponse(
		String postId,
		String memberId,
		String memberNickName,
		PostType postType,

		String title,

		String singleProblemId,
		String assessmentId,
		String contestId,

		Integer commentCount,

		LocalDateTime createdAt
	) {
		public static PostPageQueryResponse from(final PostPageQueryResult result) {
			return new PostPageQueryResponse(
				String.valueOf(result.postId()),
				String.valueOf(result.memberId()),
				result.memberNickName(),
				result.postType(),
				result.title(),
				String.valueOf(result.singleProblemId()),
				String.valueOf(result.assessmentId()),
				String.valueOf(result.contestId()),
				result.commentCount(),
				result.createdAt()
			);
		}
	}
}
