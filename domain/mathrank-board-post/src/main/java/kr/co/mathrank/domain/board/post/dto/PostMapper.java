package kr.co.mathrank.domain.board.post.dto;

import kr.co.mathrank.domain.board.post.entity.FreePost;
import kr.co.mathrank.domain.board.post.entity.Post;
import kr.co.mathrank.domain.board.post.entity.ProblemQuestionPost;
import kr.co.mathrank.domain.board.post.entity.PurchasePost;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PostMapper {
	public static PostQueryResult map(final Post post) {
		if (post instanceof FreePost freePost) {
			return new FreePostQueryResult(freePost);
		}
		if (post instanceof ProblemQuestionPost problemQuestionPost) {
			return new ProblemPostQueryResult(problemQuestionPost);
		}
		if (post instanceof PurchasePost purchasePost) {
			return new PurchasePostQueryResult(purchasePost);
		}
		log.error("[PostMapper.map]: cannot map {}", post);
		throw new IllegalArgumentException("cannot map post");
	}
}
