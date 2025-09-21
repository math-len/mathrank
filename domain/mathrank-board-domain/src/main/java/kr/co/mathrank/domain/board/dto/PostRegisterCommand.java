package kr.co.mathrank.domain.board.dto;

import org.hibernate.validator.group.GroupSequenceProvider;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

	@NotNull(groups = ValidationGroups.SingleProblemPostGroup.class)
	Long problemId,
	@NotNull(groups = ValidationGroups.AssessmentPostGroup.class)
	Long assessmentId,
	@NotNull(groups = ValidationGroups.ContestPostGroup.class)
	Long contestId
) {
	public Post toEntity() {
		return switch (postType) {
			case FREE -> Post.ofFree(title, content, memberId);
			case CONTEST -> Post.ofContest(title, content, memberId, contestId);
			case ASSESSMENT -> Post.ofAssessment(title, content, memberId, assessmentId);
			case SINGLE_PROBLEM -> Post.ofSingleProblem(title, content, memberId, problemId);
		};
	}
}
