package kr.co.mathrank.domain.board.repository;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import kr.co.mathrank.domain.board.entity.BoardCategory;
import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.outbox.EventType;
import kr.co.mathrank.domain.board.outbox.Outbox;

@SpringBootTest
class PostRepositoryTest {
	@Autowired
	private PostRepository postRepository;

	@AfterEach
	void clean() {
		postRepository.deleteAll();
	}

	@Test
	void 아웃박스_있는것만_조회한다() {
		postRepository.save(
			new FreePost("title", "content", 1L, LocalDateTime.now(), Collections.emptyList(), new Outbox(
				EventType.FREE_POST_CREATED_EVENT, LocalDateTime.now())));
		postRepository.save(
			new FreePost("title", "content", 1L, LocalDateTime.now(), Collections.emptyList()));

		Assertions.assertEquals(1, postRepository.findAllContainsOutbox().size());
	}

	@Test
	void 삭제된_놈은_조회안해() {
		final Post post = new FreePost("title", "content", 1L, LocalDateTime.now(), Collections.emptyList(), new Outbox(
			EventType.FREE_POST_CREATED_EVENT, LocalDateTime.now()));
		postRepository.save(post);

		post.setDeleted(true);
		postRepository.save(post);

		Assertions.assertTrue(postRepository.findByIdAndDeletedIsFalse(post.getId()).isEmpty());
	}

	@Test
	void 작성자로_조회할때_삭제된놈들_조회안해() {
		final long ownerId = 1L;
		for (int i = 0; i < 10; i++) {
			postRepository.save(
				new FreePost("title", "content", ownerId, LocalDateTime.now(), Collections.emptyList()));
		}
		for (int i = 0; i < 5; i++) {
			final Post post = new FreePost("title", "content", ownerId, LocalDateTime.now(), Collections.emptyList());
			post.setDeleted(true);
			postRepository.save(post);
		}

		Assertions.assertEquals(10,
			postRepository.findAllByBoardCategoryAndOwnerIdAndDeletedIsFalse(BoardCategory.FREE_BOARD, ownerId,
				Pageable.ofSize(100)).size());
	}

	@Test
	void 날짜로_조회할때_삭제된놈들_조회안해() {
		final long ownerId = 1L;
		final LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);
		for (int i = 0; i < 10; i++) {
			postRepository.save(
				new FreePost("title", "content", ownerId, now, Collections.emptyList()));
		}
		for (int i = 0; i < 5; i++) {
			final Post post = new FreePost("title", "content", ownerId, now, Collections.emptyList());
			post.setDeleted(true);
			postRepository.save(post);
		}

		Assertions.assertEquals(10,
			postRepository.findAllByBoardCategoryAndCreatedAtIsBeforeAndDeletedIsFalse(BoardCategory.FREE_BOARD, now.plusDays(1L),
				Pageable.ofSize(100)).size());
	}
}
