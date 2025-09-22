package kr.co.mathrank.app.api.board;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.board.dto.PostRegisterCommand;
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
}
