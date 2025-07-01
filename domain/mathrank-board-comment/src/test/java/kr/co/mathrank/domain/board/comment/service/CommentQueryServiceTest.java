package kr.co.mathrank.domain.board.comment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.board.comment.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.comment.dto.CommentResults;

@SpringBootTest
class CommentQueryServiceTest {
	@Autowired
	private CommentQueryService commentQueryService;
	@Autowired
	private CommentService commentService;

	@Test
	@Transactional
	void 댓글_조회_생성_오름차순() {
		// Given
		final Long postId = 1L;
		commentService.save(new CommentRegisterCommand(postId, 2L, "테스트 댓글1", List.of("image1.jpg", "image2.jpg")));
		commentService.save(new CommentRegisterCommand(postId, 2L, "테스트 댓글2", List.of("image1.jpg", "image2.jpg")));
		commentService.save(new CommentRegisterCommand(postId, 2L, "테스트 댓글3", List.of("image1.jpg", "image2.jpg")));

		// When
		final CommentResults results = commentQueryService.queryComments(postId);

		// Then
		assertEquals("테스트 댓글1", results.comments().get(0).content());
	}
}