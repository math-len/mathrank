package kr.co.mathrank.app.api.board;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.board.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.dto.CommentUpdateCommand;
import kr.co.mathrank.domain.board.dto.PostRegisterCommand;
import kr.co.mathrank.domain.board.dto.PostUpdateCommand;
import kr.co.mathrank.domain.board.entity.PostType;

public class Requests {
	record PostSaveRequest(
		@NotNull
		@Schema(description = "NOTICE는 ADMIN만 가능")
		PostType postType,
		@NotEmpty
		String title,
		@NotEmpty
		String content,
		@Schema(
			description = "postType=SINGLE_PROBLEM 일 때 필수",
			example = "1"
		)
		Long problemId,

		@Schema(
			description = "postType=ASSESSMENT 일 때 필수",
			example = "1"
		)
		Long assessmentId,

		@Schema(
			description = "postType=CONTEST 일 때 필수",
			example = "1"
		)
		Long contestId
	) {
		public PostRegisterCommand toCommand(final Long memberId, final Role role) {
			return new PostRegisterCommand(
				postType,
				title,
				content,
				memberId,
				role,
				problemId,
				assessmentId,
				contestId
			);
		}
	}

	record PostUpdateRequest(
		@NotEmpty
		String title,
		@NotEmpty
		String content
	) {
		public PostUpdateCommand toCommand(final Long postId, final Long memberId) {
			return new PostUpdateCommand(postId, memberId, title, content);
		}
	}

	record CommentSaveRequest(
		@NotNull
		String content
	) {
		public CommentRegisterCommand toCommand(final Long postId, final Long memberId) {
			return new CommentRegisterCommand(
				postId,
				memberId,
				content
			);
		}
	}

	record CommentUpdateRequest(
		@NotNull
		Long commentId,
		@NotNull
		String content
	) {
		CommentUpdateCommand toCommand(final Long memberId) {
			return new CommentUpdateCommand(
				commentId,
				memberId,
				content
			);
		}
	}
}
