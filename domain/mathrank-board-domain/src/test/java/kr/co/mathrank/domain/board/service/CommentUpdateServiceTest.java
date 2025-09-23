package kr.co.mathrank.domain.board.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.domain.board.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.dto.CommentUpdateCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotFoundCommentException;
import kr.co.mathrank.domain.board.exception.CannotUpdateCommentException;
import kr.co.mathrank.domain.board.repository.CommentRepository;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest
class CommentUpdateServiceTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private CommentUpdateService commentUpdateService;
	@Autowired
	private CommentRegisterService commentRegisterService;
	@Autowired
	private CommentRepository commentRepository;

	@Test
	void 댓글을_못찾으면_예외() {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();
		final Long commentId = commentRegisterService.register(
			new CommentRegisterCommand(postId, userId, "testContent"));
		final Long anotherCommentId = commentId + 1;

		// 다른 commentId로 조회
		Assertions.assertThrows(CannotFoundCommentException.class, () -> commentUpdateService.update(
			new CommentUpdateCommand(anotherCommentId, userId, "testContent1")));
	}

	@Test
	void 본인_댓글이_아니면_수정_불가() {
		final Long userId = 1L;
		final Long anotherUserId = 2L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();
		final Long commentId = commentRegisterService.register(
			new CommentRegisterCommand(postId, userId, "testContent"));

		// 다른 사용자로 업데이트
		Assertions.assertThrows(CannotUpdateCommentException.class, () -> commentUpdateService.update(
			new CommentUpdateCommand(commentId, anotherUserId, "testContent1")));
	}

	@Test
	void 본인_댓글_수정은_성공() {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();

		final String beforeContent = "beforeContent";
		final String afterContent = "afterContent";

		final Long commentId = commentRegisterService.register(
			new CommentRegisterCommand(postId, userId, beforeContent));

		// 정상 업데이트
		commentUpdateService.update(new CommentUpdateCommand(commentId, userId, afterContent));

		Assertions.assertEquals(afterContent, commentRepository.findById(commentId).get().getContent());
	}
}
