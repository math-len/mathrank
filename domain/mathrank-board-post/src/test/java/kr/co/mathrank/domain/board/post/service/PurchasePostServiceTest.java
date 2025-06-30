package kr.co.mathrank.domain.board.post.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.domain.board.post.dto.PurchasePostCreateCommand;
import kr.co.mathrank.domain.board.post.entity.PurchasePost;
import kr.co.mathrank.domain.board.post.repository.PostRepository;

@SpringBootTest
class PurchasePostServiceTest {
	@Autowired
	private PostRegisterService postService;
	@Autowired
	private PostRepository postRepository;

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

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

		final PurchasePostCreateCommand command = new PurchasePostCreateCommand(memberId, title, content, images,
			problemId);
		postService.save(command);

		final PurchasePost post = (PurchasePost)postRepository.findAll().getFirst();

		Assertions.assertAll(
			() -> Assertions.assertEquals(memberId, post.getOwnerId()),
			() -> Assertions.assertEquals(title, post.getTitle()),
			() -> Assertions.assertEquals(content, post.getContent()),
			() -> Assertions.assertEquals(images, post.getImages()),
			() -> Assertions.assertEquals(problemId, post.getPurchaseId())
		);
	}

	@Test
	void 형식오류시_게시글_생성_실패() {
		final PurchasePostCreateCommand noContent = new PurchasePostCreateCommand(1L, "test", null, List.of("test"),
			1L);
		final PurchasePostCreateCommand emptyContent = new PurchasePostCreateCommand(1L, "test", "", List.of("test"),
			1L);
		final PurchasePostCreateCommand noTitle = new PurchasePostCreateCommand(1L, null, "content", List.of("test"),
			1L);
		final PurchasePostCreateCommand emptyTitle = new PurchasePostCreateCommand(1L, "", "content", List.of("test"),
			1L);
		final PurchasePostCreateCommand nullImages = new PurchasePostCreateCommand(1L, "test", "test", null, 1L);
		final PurchasePostCreateCommand exceedImages = new PurchasePostCreateCommand(1L, "test", "test",
			List.of("1", "1", "1", "1", "1", "1"), 1L);
		final PurchasePostCreateCommand noUser = new PurchasePostCreateCommand(null, "test", "test", List.of("test"),
			1L);
		final PurchasePostCreateCommand noQuestionId = new PurchasePostCreateCommand(1L, "test", "test",
			List.of("test"), null);

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