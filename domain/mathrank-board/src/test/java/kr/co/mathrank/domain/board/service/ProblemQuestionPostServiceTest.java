package kr.co.mathrank.domain.board.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.domain.board.dto.ProblemQuestionPostCreateCommand;
import kr.co.mathrank.domain.board.entity.ProblemQuestionPost;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest(properties = """
	de.flapdoodle.mongodb.embedded.version=6.0.5
	spring.data.mongodb.auto-index-creation=true
	logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
	snowflake.node.id=1""")
class ProblemQuestionPostServiceTest {
	@Autowired
	private PostRegisterService postService;
	@Autowired
	private PostRepository postRepository;

	@AfterEach
	void clean() {
		postRepository.deleteAll();
	}

	@Test
	void 조회_후_캐스팅_정상동작_확인() {
		final Long memberId = 1L;
		final String title = "title";
		final String content = "content";
		final List<String> images = List.of("image1", "image2", "image3");
		final Long problemId = 1L;

		final ProblemQuestionPostCreateCommand command = new ProblemQuestionPostCreateCommand(memberId, title, content, images, problemId);
		postService.save(command);

		final ProblemQuestionPost post = (ProblemQuestionPost) postRepository.findAll().getFirst();

		Assertions.assertAll(
			() -> Assertions.assertEquals(memberId, post.getOwnerId()),
			() -> Assertions.assertEquals(title, post.getTitle()),
			() -> Assertions.assertEquals(content, post.getContent()),
			() -> Assertions.assertEquals(images, post.getImages()),
			() -> Assertions.assertEquals(problemId, post.getQuestionId())
		);
	}

	@Test
	void 형식오류시_게시글_생성_실패() {
		final ProblemQuestionPostCreateCommand noContent = new ProblemQuestionPostCreateCommand(1L, "test", null, List.of("test"), 1L);
		final ProblemQuestionPostCreateCommand emptyContent = new ProblemQuestionPostCreateCommand(1L, "test", "", List.of("test"), 1L);
		final ProblemQuestionPostCreateCommand noTitle = new ProblemQuestionPostCreateCommand(1L, null, "content", List.of("test"), 1L);
		final ProblemQuestionPostCreateCommand emptyTitle = new ProblemQuestionPostCreateCommand(1L, "", "content", List.of("test"), 1L);
		final ProblemQuestionPostCreateCommand nullImages = new ProblemQuestionPostCreateCommand(1L, "test", "test", null, 1L);
		final ProblemQuestionPostCreateCommand exceedImages = new ProblemQuestionPostCreateCommand(1L, "test", "test",
			List.of("1", "1", "1", "1", "1", "1"), 1L);
		final ProblemQuestionPostCreateCommand noUser = new ProblemQuestionPostCreateCommand(null, "test", "test", List.of("test"), 1L);
		final ProblemQuestionPostCreateCommand noQuestionId = new ProblemQuestionPostCreateCommand(1L, "test", "test", List.of("test"), null);

		Assertions.assertAll(
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(noContent)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(emptyContent)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(noTitle)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(emptyTitle)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(nullImages)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(exceedImages)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(noUser)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(noQuestionId))
		);
	}
}