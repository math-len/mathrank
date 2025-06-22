package kr.co.mathrank.domain.board.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.domain.board.dto.FreePostCreateCommand;
import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest
class FreePostServiceTest {
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

		final FreePostCreateCommand command = new FreePostCreateCommand(memberId, title, content, images);
		postService.save(command);

		final FreePost post = (FreePost) postRepository.findAll().getFirst();

		Assertions.assertAll(
			() -> Assertions.assertEquals(memberId, post.getOwnerId()),
			() -> Assertions.assertEquals(title, post.getTitle()),
			() -> Assertions.assertEquals(content, post.getContent()),
			() -> Assertions.assertEquals(images, post.getImages())
		);
	}

	@Test
	void 형식오류시_게시글_생성_실패() {
		final FreePostCreateCommand noContent = new FreePostCreateCommand(1L, "test", null, List.of("test"));
		final FreePostCreateCommand emptyContent = new FreePostCreateCommand(1L, "test", "", List.of("test"));
		final FreePostCreateCommand noTitle = new FreePostCreateCommand(1L, null, "content", List.of("test"));
		final FreePostCreateCommand emptyTitle = new FreePostCreateCommand(1L, "", "content", List.of("test"));
		final FreePostCreateCommand nullImages = new FreePostCreateCommand(1L, "test", "test", null);
		final FreePostCreateCommand exceedImages = new FreePostCreateCommand(1L, "test", "test",
			List.of("1", "1", "1", "1", "1", "1"));
		final FreePostCreateCommand noUser = new FreePostCreateCommand(null, "test", "test", List.of("test"));

		Assertions.assertAll(
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(noContent)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(emptyContent)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(noTitle)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(emptyTitle)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(nullImages)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(exceedImages)),
			() -> Assertions.assertThrows(ConstraintViolationException.class, () -> postService.save(noUser))
		);
	}
}