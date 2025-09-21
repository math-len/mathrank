package kr.co.mathrank.domain.board.dto;

import org.hibernate.validator.group.GroupSequenceProvider;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.board.constraints.NoticePostConstraint;
import kr.co.mathrank.domain.board.constraints.PostGroupSequenceProvider;
import kr.co.mathrank.domain.board.constraints.ValidationGroups;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.PostType;

@GroupSequenceProvider(PostGroupSequenceProvider.class)
public record PostRegisterCommand(
	@NotNull
	PostType postType,
	@NotEmpty
	String title,
	@NotEmpty
	String content,
	@NotNull
	Long memberId,

	@NoticePostConstraint(groups = ValidationGroups.NoticePostGroup.class)
	Role memberRole,

	@NotNull(groups = ValidationGroups.SingleProblemPostGroup.class)
	Long problemId,
	@NotNull(groups = ValidationGroups.AssessmentPostGroup.class)
	Long assessmentId,
	@NotNull(groups = ValidationGroups.ContestPostGroup.class)
	Long contestId
) {
	public Post toEntity() {
		final Post.PostBuilder postBuilder = Post.builder()
			.title(title)
			.content(content)
			.memberId(memberId);

		final Post.PostBuilder post = switch (postType) {
			case FREE -> postBuilder
				.postType(PostType.FREE);
			case CONTEST -> postBuilder
				.contestId(contestId)
				.postType(PostType.CONTEST);
			case ASSESSMENT -> postBuilder
				.assessmentId(assessmentId)
				.postType(PostType.ASSESSMENT);
			case SINGLE_PROBLEM -> postBuilder
				.postType(PostType.SINGLE_PROBLEM)
				.singleProblemId(problemId);
			case NOTICE -> postBuilder
				.postType(PostType.NOTICE);
		};

		return post.build();
	}
}
