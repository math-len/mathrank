package kr.co.mathrank.domain.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.board.dto.CommentDeleteCommand;
import kr.co.mathrank.domain.board.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotDeleteCommentException;
import kr.co.mathrank.domain.board.exception.CannotFoundCommentException;
import kr.co.mathrank.domain.board.repository.CommentRepository;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest(properties = """
	spring.jpa.show-sql=true
	spring.jpa.properties.hibernate.format_sql=true
	""")
class CommentDeleteServiceTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private CommentRegisterService commentRegisterService;
	@Autowired
	private CommentDeleteService commentDeleteService;
	@Autowired
	private CommentRepository commentRepository;

	@Test
	@Transactional
	void 댓글을_못찾으면_예외() {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();
		final Long commentId = commentRegisterService.register(
			new CommentRegisterCommand(postId, userId, "testContent"));
		final Long anotherCommentId = commentId + 1;

		// 다른 commentId로 조회
		Assertions.assertThrows(CannotFoundCommentException.class, () -> commentDeleteService.delete(
			new CommentDeleteCommand(anotherCommentId, userId, Role.USER)));
	}

	@Test
	@Transactional
	void 본인_댓글이_아니면_삭제_불가() {
		final Long userId = 1L;
		final Long anotherUserId = 2L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();
		final Long commentId = commentRegisterService.register(
			new CommentRegisterCommand(postId, userId, "testContent"));

		// 다른 사용자로 업데이트
		Assertions.assertThrows(CannotDeleteCommentException.class, () -> commentDeleteService.delete(
			new CommentDeleteCommand(commentId, anotherUserId, Role.USER)));
	}

	@Test
	@Transactional
	void 관리자는_본인게_아니더라도_삭제_가능하다() {
		final Long userId = 1L;
		final Long anotherUserId = 2L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();
		final Long commentId = commentRegisterService.register(
			new CommentRegisterCommand(postId, userId, "testContent"));

		// 다른 사용자로 업데이트 ( 관리자임 )
		Assertions.assertDoesNotThrow(() -> commentDeleteService.delete(
			new CommentDeleteCommand(commentId, anotherUserId, Role.ADMIN)));
	}

	@Test
	void 동시에_같은_댓글_삭제할때_댓글_수_업데이트_정상동작() throws InterruptedException {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();

		final Long commentId = commentRegisterService.register(new CommentRegisterCommand(postId, userId, "testContent"));
		commentRegisterService.register(new CommentRegisterCommand(postId, userId, "testContent"));

		// 하나를 두번 동시에 삭제 시도하자
		final int deleteTryCount = 2;

		// 2개 쓰레드
		final ExecutorService executor = Executors.newFixedThreadPool(2);
		final CountDownLatch latch = new CountDownLatch(deleteTryCount);

		for (int i = 0; i < deleteTryCount; i++) {
			executor.submit(() -> {
				try {
					commentDeleteService.delete(
						new CommentDeleteCommand(commentId, userId, Role.USER));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Assertions.assertEquals(1, postRepository.findById(postId).get().getCommentCount());

		postRepository.deleteAll();
	}

	@Test
	void 동시에_댓글_삭제됐을때_댓글수_정상_업데이트() throws InterruptedException {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.of("title", "content", userId))
			.getId();

		// 원래 댓글 수
		final int commentCount = 100;

		// 댓글 다 등록
		final List<Long> commentIds = new ArrayList<>();
		for (int i = 0; i < commentCount; i++) {
			commentIds.add(commentRegisterService.register(new CommentRegisterCommand(postId, userId, "testContent")));
		}

		// 이중 30개만 삭제하자
		final int deleteCount = 30;

		// 10개 쓰레드
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(deleteCount);

		for (int i = 0; i < deleteCount; i++) {
			final int commentIndex = i;
			executor.submit(() -> {
				try {
					commentDeleteService.delete(
						new CommentDeleteCommand(commentIds.get(commentIndex), userId, Role.USER));
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Assertions.assertEquals(commentCount - deleteCount, postRepository.findById(postId).get().getCommentCount());

		postRepository.deleteAll();
	}
}
