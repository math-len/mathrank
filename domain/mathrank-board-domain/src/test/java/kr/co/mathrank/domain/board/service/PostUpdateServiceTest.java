package kr.co.mathrank.domain.board.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.domain.board.dto.PostUpdateCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotUpdatePostException;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest
@Transactional
class PostUpdateServiceTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private PostUpdateService postUpdateService;

	@Test
	void 본인이_수정할땐_성공() {
		final Long userId = 1L;
		final Long postId = postRepository.save(Post.ofFree("title", "content", userId))
			.getId();

		postUpdateService.update(new PostUpdateCommand(postId, userId, "newTitle", "newContent"));

		final Post post = postRepository.findById(postId).get();

		Assertions.assertAll(
			() -> Assertions.assertEquals("newTitle", post.getTitle()),
			() -> Assertions.assertEquals("newContent", post.getContent())
		);
	}

	@Test
	void 어드민이든_뭐든_본인게_아니면_수정_불가() {
		final Long userId = 1L;
		final Long anotherUserId = 2L;
		final Long postId = postRepository.save(Post.ofFree("title", "content", userId))
			.getId();

		postUpdateService.update(new PostUpdateCommand(postId, userId, "newTitle", "newContent"));

		Assertions.assertAll(
			() -> Assertions.assertThrows(CannotUpdatePostException.class, () -> postUpdateService.update(
				new PostUpdateCommand(postId, anotherUserId, "newTitle", "newContent")
			))
		);

	}
}
