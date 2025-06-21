package kr.co.mathrank.domain.board.service;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import kr.co.mathrank.domain.board.dto.FreePostQueryResult;
import kr.co.mathrank.domain.board.dto.ProblemPostQueryResult;
import kr.co.mathrank.domain.board.dto.PurchasePostQueryResult;
import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.ProblemQuestionPost;
import kr.co.mathrank.domain.board.entity.PurchasePost;

class PostMapperTest {
	@Test
	void DTO매핑_확인() {
		final Post freePost = new FreePost("title", "content", 1L, LocalDateTime.now(), Collections.emptyList());
		final Post purchasePost = new PurchasePost("title", "content", 1L, LocalDateTime.now(), Collections.emptyList(), 2L);
		final Post questionPost = new ProblemQuestionPost("title", "content", 1L, LocalDateTime.now(), Collections.emptyList(), 1L);

		Assertions.assertAll(
			() -> Assertions.assertInstanceOf(FreePostQueryResult.class, PostMapper.map(freePost)),
			() -> Assertions.assertInstanceOf(PurchasePostQueryResult.class, PostMapper.map(purchasePost)),
			() -> Assertions.assertInstanceOf(ProblemPostQueryResult.class, PostMapper.map(questionPost))
		);
	}
}