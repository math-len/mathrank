package kr.co.mathrank.domain.board.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
class CommentRegisterServiceTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private CommentRegisterService commentRegisterService;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Transactional
	void 게시글을_찾을_수_없으면_예외발생() {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();
		final Long anotherPostId = 1000L;

		Assertions.assertThrows(CannotFoundPostException.class,
			() -> commentRegisterService.register(new CommentRegisterCommand(anotherPostId, userId, "testContent")));
	}

	@Test
	@Transactional
	void 댓글_작성_시_아이디_반환한다() {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();

		Assertions.assertNotNull(commentRegisterService.register(new CommentRegisterCommand(postId, userId, "testContent")));
	}

	@Test
	void 동시에_댓글_작성되도_댓글수_정상_업데이트() throws InterruptedException {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();

		final int tryCount = 100;
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(tryCount);

		for (int i = 0; i < tryCount; i++) {
			executor.submit(() -> {
				try {
					commentRegisterService.register(new CommentRegisterCommand(postId, userId, "testContent"));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Assertions.assertEquals(tryCount, postRepository.findById(postId).get().getCommentCount());

		postRepository.deleteAll();
	}
}
