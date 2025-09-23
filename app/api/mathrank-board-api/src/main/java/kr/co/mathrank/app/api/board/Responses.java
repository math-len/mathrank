package kr.co.mathrank.app.api.board;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.mathrank.client.internal.member.MemberInfo;
import kr.co.mathrank.domain.board.dto.CommentDetailResult;
import kr.co.mathrank.domain.board.dto.PostDetailQueryResult;
import kr.co.mathrank.domain.board.entity.PostType;

public class Responses {
	record PostDetailResponse(
		Long postId,
		MemberResponse memberInfo,
		PostType postType,
		String title,
		String content,
		List<CommentDetailResponse> comments,
		Long singleProblemId,
		Long assessmentId,
		Long contestId,
		LocalDateTime createdAt
	) {
		public static PostDetailResponse from(final PostDetailQueryResult postResult, final List<CommentDetailResponse> comments) {
			return new PostDetailResponse(
				postResult.postId(),
				MemberResponse.from(postResult.memberId(), postResult.memberNickName()),
				postResult.postType(),
				postResult.title(),
				postResult.content(),
				comments,
				postResult.singleProblemId(),
				postResult.assessmentId(),
				postResult.contestId(),
				postResult.createdAt()
			);
		}
	}

	record CommentDetailResponse(
		Long commentId,
		MemberResponse memberInfo,
		String content,
		LocalDateTime createdAt
	) {
		public static CommentDetailResponse from(final CommentDetailResult commentDetailResult, final MemberInfo memberInfo) {
			return new CommentDetailResponse(
				commentDetailResult.commentId(),
				MemberResponse.from(memberInfo),
				commentDetailResult.content(),
				commentDetailResult.createdAt()
			);
		}
	}

	record MemberResponse(
		Long memberId,
		String nickName
	) {
		public static MemberResponse from(final MemberInfo memberInfo) {
			return new MemberResponse(
				memberInfo.memberId(),
				memberInfo.memberName()
			);
		}

		// Post의 경우, 닉네임 기반 조회라 client를 통한 닉네임 조회 필요 없음
		public static MemberResponse from(final Long memberId, final String nickName) {
			return new MemberResponse(
				memberId,
				nickName
			);
		}
	}
}
