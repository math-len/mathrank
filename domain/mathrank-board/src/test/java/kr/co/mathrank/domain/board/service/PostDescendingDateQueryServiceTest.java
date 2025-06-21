package kr.co.mathrank.domain.board.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.domain.board.dto.DescendingDateQueryCommand;
import kr.co.mathrank.domain.board.dto.PostQueryResult;
import kr.co.mathrank.domain.board.entity.BoardCategory;
import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.entity.ProblemQuestionPost;
import kr.co.mathrank.domain.board.entity.PurchasePost;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest(properties = """
	de.flapdoodle.mongodb.embedded.version=6.0.5
	spring.data.mongodb.auto-index-creation=true
	logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
	""")
class PostDescendingDateQueryServiceTest {
	@Autowired
	private PostDescendingDateQueryService postDescendingDateQueryService;
	@Autowired
	private PostRepository postRepository;

	@AfterEach
	void clean() {
		postRepository.deleteAll();
	}

	@Test
	void 결과가_내림차순으로_정렬되는지() {
		final LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

		for (int i = 0; i < 100; i++) {
			postRepository.save(
				new FreePost("title", "content", 1L, now.plus(i, ChronoUnit.SECONDS), Collections.emptyList()));
		}

		final List<PostQueryResult> posts = postDescendingDateQueryService.queryPostsByDateDescending(
			new DescendingDateQueryCommand(BoardCategory.FREE_BOARD, now.plus(99, ChronoUnit.SECONDS), 5)).results();

		Assertions.assertTrue(posts.getFirst().getCreatedAt().isAfter(posts.getLast().getCreatedAt()));
	}

	@Test
	void 특정_게시판만_조회() {
		final LocalDateTime before = LocalDateTime.of(2020, 1, 1, 0, 0);

		for (int i = 0; i < 1; i++) {
			postRepository.save(new FreePost("title", "content", 1L, before, Collections.emptyList()));
		}
		for (int i = 0; i < 2; i++) {
			postRepository.save(new PurchasePost("title", "content", 1L, before, Collections.emptyList(), 1L));
		}
		for (int i = 0; i < 3; i++) {
			postRepository.save(new ProblemQuestionPost("title", "content", 1L, before, Collections.emptyList(), 1L));
		}

		final LocalDateTime now = before.plus(1, ChronoUnit.SECONDS);

		Assertions.assertAll(
			() -> Assertions.assertEquals(1, postDescendingDateQueryService.queryPostsByDateDescending(
				new DescendingDateQueryCommand(BoardCategory.FREE_BOARD, now, 100)).results().size()),
			() -> Assertions.assertEquals(2,
				postDescendingDateQueryService.queryPostsByDateDescending(
					new DescendingDateQueryCommand(BoardCategory.PURCHASE_QUESTION, now, 100)).results().size()),
			() -> Assertions.assertEquals(3,
				postDescendingDateQueryService.queryPostsByDateDescending(
					new DescendingDateQueryCommand(BoardCategory.PROBLEM_QUESTION, now, 100)).results().size())
		);
	}
}