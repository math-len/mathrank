package kr.co.mathrank.domain.board.repository;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.outbox.Outbox;
import kr.co.mathrank.domain.board.outbox.EventType;

@SpringBootTest(properties = """
	de.flapdoodle.mongodb.embedded.version=6.0.5
	spring.data.mongodb.auto-index-creation=true
	logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
	snowflake.node.id=1
	""")
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
}
