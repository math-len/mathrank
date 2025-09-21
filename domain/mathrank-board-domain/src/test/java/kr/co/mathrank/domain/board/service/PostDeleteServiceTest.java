package kr.co.mathrank.domain.board.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.board.dto.PostDeleteCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotFoundPostException;
import kr.co.mathrank.domain.board.exception.CannotDeletePostException;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest
class PostDeleteServiceTest {
	@Autowired
	private PostDeleteService postDeleteService;
	@Autowired
	private PostRepository postRepository;

	@Test
	@Transactional
	void 게시글을_찾을_수_없으면_예외발생() {
		final Long userId = 1L;
		final Long postId = postRepository.save(new Post("title", "content", userId))
			.getId();
		final Long anotherPostId = 1000L;

		Assertions.assertThrows(CannotFoundPostException.class,
			() -> postDeleteService.delete(new PostDeleteCommand(anotherPostId, userId, Role.USER)));
	}

	@Test
	@Transactional
	void 일반_사용자가_본인_게시글_삭제_가능하다() {
		final Long userId = 1L;
		final Long postId = postRepository.save(new Post("title", "content", userId))
			.getId();

		postDeleteService.delete(new PostDeleteCommand(postId, userId, Role.USER));

		Assertions.assertTrue(postRepository.findById(postId).isEmpty());
	}

	@Test
	@Transactional
	void 일반_사용자가_본인_게시글외_삭제시_예외발생() {
		final Long userId = 1L;
		final Long otherUserId = 2L;
		final Long postId = postRepository.save(new Post("title", "content", userId))
			.getId();

		Assertions.assertThrows(CannotDeletePostException.class,
			() -> postDeleteService.delete(new PostDeleteCommand(postId, otherUserId, Role.USER)));
	}

	@Test
	void 어드민은_모두_삭제_가능() {
		final Long userId = 1L;
		final Long otherUserId = 2L;
		final Long postId = postRepository.save(new Post("title", "content", userId))
			.getId();

		Assertions.assertDoesNotThrow(
			() -> postDeleteService.delete(new PostDeleteCommand(postId, otherUserId, Role.ADMIN)));
	}
}
