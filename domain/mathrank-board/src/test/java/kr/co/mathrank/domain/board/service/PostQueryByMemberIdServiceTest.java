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

import kr.co.mathrank.domain.board.dto.PostQueryResult;
import kr.co.mathrank.domain.board.dto.QueryPostByOwnerIdCommand;
import kr.co.mathrank.domain.board.entity.BoardCategory;
import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.entity.PurchasePost;
import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest(properties = """
	de.flapdoodle.mongodb.embedded.version=6.0.5
	spring.data.mongodb.auto-index-creation=true
	logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
	snowflake.node.id=1
	""")
class PostQueryByMemberIdServiceTest {
	@Autowired
	private PostQueryService postQueryService;
	@Autowired
	private PostRepository postRepository;

	@AfterEach
	void clean() {
		postRepository.deleteAll();
	}

	@Test
	void 같은_게시판에서_본인_게시글만_조회된다() {
		final Long ownerId = 1L;
		final Long otherId = 2L;
		final LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

		for (int i = 0; i < 10; i++) {
			postRepository.save(
				new FreePost("title", "content", ownerId, now.plus(i, ChronoUnit.SECONDS), Collections.emptyList()));
		}
		for (int i = 0; i < 20; i++) {
			postRepository.save(
				new FreePost("title", "content", otherId, now.plus(i, ChronoUnit.SECONDS), Collections.emptyList()));
		}

		Assertions.assertEquals(10, postQueryService.queryByOwnerIdAndDateDescending(
			new QueryPostByOwnerIdCommand(ownerId, BoardCategory.FREE_BOARD, 30)).results().size());
	}

	@Test
	void 다른_게시판의_본인글은_조회되지_않는다() {
		final Long ownerId = 1L;
		final LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

		for (int i = 0; i < 10; i++) {
			postRepository.save(
				new FreePost("title", "content", ownerId, now.plus(i, ChronoUnit.SECONDS), Collections.emptyList()));
		}
		for (int i = 0; i < 20; i++) {
			postRepository.save(
				new PurchasePost("title", "content", ownerId, now.plus(i, ChronoUnit.SECONDS), Collections.emptyList(), 1L));
		}

		Assertions.assertAll(
			() -> Assertions.assertEquals(10, postQueryService.queryByOwnerIdAndDateDescending(
				new QueryPostByOwnerIdCommand(ownerId, BoardCategory.FREE_BOARD, 30)).results().size()),
			() -> Assertions.assertEquals(20, postQueryService.queryByOwnerIdAndDateDescending(
				new QueryPostByOwnerIdCommand(ownerId, BoardCategory.PURCHASE_QUESTION, 30)).results().size())
		);
	}

	@Test
	void 조회는_등록날짜_내림차순으로_정렬된다() {
		final Long ownerId = 1L;
		final LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

		for (int i = 0; i < 100; i++) {
			postRepository.save(
				new FreePost("title", "content", 1L, now.plus(i, ChronoUnit.SECONDS), Collections.emptyList()));
		}

		final List<PostQueryResult> posts = postQueryService.queryByOwnerIdAndDateDescending(
			new QueryPostByOwnerIdCommand(ownerId, BoardCategory.FREE_BOARD, 30)).results();

		Assertions.assertTrue(posts.getFirst().getCreatedAt().isAfter(posts.getLast().getCreatedAt()));
	}
}