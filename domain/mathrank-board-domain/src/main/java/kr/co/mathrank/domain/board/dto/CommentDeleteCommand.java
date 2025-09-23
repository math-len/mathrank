package kr.co.mathrank.domain.board.dto;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;

public record CommentDeleteCommand(
	@NotNull
	Long commentId,
	@NotNull
	Long requestMemberId,
	@NotNull
	Role requestMemberRole
) {
}
