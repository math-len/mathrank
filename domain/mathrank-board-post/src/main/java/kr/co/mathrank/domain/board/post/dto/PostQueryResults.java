package kr.co.mathrank.domain.board.post.dto;

import java.util.List;

import kr.co.mathrank.domain.board.post.entity.Post;

public record PostQueryResults(
	List<PostQueryResult> results
) {
	public static PostQueryResults from(List<Post> posts) {
		return new PostQueryResults(posts.stream()
			.map(PostMapper::map)
			.toList());
	}
}
