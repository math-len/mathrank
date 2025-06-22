package kr.co.mathrank.domain.board.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.domain.board.dto.FreePostCreateCommand;
import kr.co.mathrank.domain.board.dto.PostUpdateCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest(properties = """
	de.flapdoodle.mongodb.embedded.version=6.0.5
	spring.data.mongodb.auto-index-creation=true
	logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
	snowflake.node.id=1""")
class PostUpdateServiceTest {
	@Autowired
	private PostUpdateService postUpdateService;
	@Autowired
	private PostRegisterService registerService;
	@Autowired
	private PostRepository postRepository;

	@AfterEach
	void clean() {
		postRepository.deleteAll();
	}

	@Test
	void 업데이트_COMMAND의_필드로_모두_업데이트_된다() {
		final long memberId = 1L;
		final String id = registerService.save(
			new FreePostCreateCommand(memberId, "title", "content", List.of("images")));

		final String newTitle = "newTitle";
		final String newContent = "newContent";
		postUpdateService.update(new PostUpdateCommand(id, memberId, newTitle, newContent, List.of("images")));

		final Post post = postRepository.findById(id).get();

		Assertions.assertAll(
			() -> Assertions.assertEquals(newContent, post.getContent()),
			() -> Assertions.assertEquals(newTitle, post.getTitle())
		);
	}

	@Test
	void 소유자가_아니면_변경하지_못한다() {
		final long memberId = 1L;
		final long anotherMemberId = 2L;
		final String id = registerService.save(
			new FreePostCreateCommand(memberId, "title", "content", List.of("images")));

		final String newTitle = "newTitle";
		final String newContent = "newContent";

		Assertions.assertThrows(IllegalArgumentException.class, () -> postUpdateService.update(
			new PostUpdateCommand(id, anotherMemberId, newTitle, newContent, List.of("images"))));
	}

	@Test
	void post_찾을수_없을때_예외처리() {
		final long memberId = 1L;
		registerService.save(
			new FreePostCreateCommand(memberId, "title", "content", List.of("images")));

		final String newTitle = "newTitle";
		final String newContent = "newContent";

		Assertions.assertThrows(IllegalArgumentException.class, () -> postUpdateService.update(
			new PostUpdateCommand("randomId", memberId, newTitle, newContent, List.of("images"))));
	}
}