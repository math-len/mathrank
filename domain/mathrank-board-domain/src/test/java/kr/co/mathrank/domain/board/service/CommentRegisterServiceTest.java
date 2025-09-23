package kr.co.mathrank.domain.board.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.mathrank.domain.board.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotFoundPostException;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest
@Transactional
class CommentRegisterServiceTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private CommentRegisterService commentRegisterService;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void 게시글을_찾을_수_없으면_예외발생() {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();
		final Long anotherPostId = 1000L;

		Assertions.assertThrows(CannotFoundPostException.class,
			() -> commentRegisterService.register(new CommentRegisterCommand(anotherPostId, userId, "testContent")));
	}

	@Test
	void 댓글_작성_시_아이디_반환한다() {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();

		Assertions.assertNotNull(commentRegisterService.register(new CommentRegisterCommand(postId, userId, "testContent")));
	}
}
