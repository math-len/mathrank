package kr.co.mathrank.domain.board.service;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.domain.board.dto.PostDeleteCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.ProblemQuestionPost;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest
class PostDeleteServiceTest {
	@Autowired
	private PostDeleteService postDeleteService;
	@Autowired
	private PostRepository postRepository;

	@AfterEach
	void clean() {
		postRepository.deleteAll();
	}

	@Test
	void 본인_게시글이면_삭제된다() {
		final Long ownerId = 1L;
		final Post post = new ProblemQuestionPost("test", "content", ownerId, LocalDateTime.of(2020, 11, 21, 11, 20),
			Collections.emptyList(), 2L);
		postRepository.save(post);
		postDeleteService.delete(new PostDeleteCommand(ownerId, post.getId()));

		Assertions.assertTrue(postRepository.findByIdAndDeletedIsFalse(post.getId()).isEmpty());
	}

	@Test
	void 본인_아니면_삭제_불가능하다() {
		final Long ownerId = 1L;
		final Long otherId = 2L;

		final Post post = new ProblemQuestionPost("test", "content", ownerId, LocalDateTime.of(2020, 11, 21, 11, 20),
			Collections.emptyList(), 2L);
		postRepository.save(post);

		Assertions.assertThrows(IllegalArgumentException.class, () -> postDeleteService.delete(new PostDeleteCommand(otherId, post.getId())));
	}

	@Test
	void 없는_게시글이면_에러() {
		final Long ownerId = 1L;
		final Long otherId = 2L;

		final Post post = new ProblemQuestionPost("test", "content", ownerId, LocalDateTime.of(2020, 11, 21, 11, 20),
			Collections.emptyList(), 2L);
		postRepository.save(post);

		Assertions.assertThrows(IllegalArgumentException.class, () -> postDeleteService.delete(new PostDeleteCommand(otherId, post.getId() + "1")));
	}
}