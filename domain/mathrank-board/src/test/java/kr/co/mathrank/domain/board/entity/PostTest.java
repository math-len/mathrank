package kr.co.mathrank.domain.board.entity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;

import kr.co.mathrank.domain.board.repository.PostRepository;

@SpringBootTest(properties = """
	de.flapdoodle.mongodb.embedded.version=6.0.5
	spring.data.mongodb.auto-index-creation=true
	""")
class PostTest {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PostRepository postRepository;

	@Test
	void 인덱스_갯수_확인() {
		List<IndexInfo> indexList = mongoTemplate.indexOps(Post.class).getIndexInfo();

		Assertions.assertEquals(5, indexList.size());
	}

	@Test
	void 인덱스_idx_boardCategory_ownerId_사용() {
		for (int i = 0; i < 100; i++) {
			postRepository.save(new FreePost("test", "content", 1L, LocalDateTime.now(), Collections.emptyList()));
		}

		final Map<String, String> doc = new HashMap<>();
		doc.put("ownerId", "test");
		doc.put("boardCategory", "FREE_BOARD");

		Document document = mongoTemplate.getCollection("posts")
			.find(new Document(doc))
			.explain();

		Document queryPlanner = (Document)document.get("queryPlanner");
		Document winningPlan = (Document)queryPlanner.get("winningPlan");
		Document inputStage = (Document)winningPlan.get("inputStage");

		Assertions.assertAll(
			() -> Assertions.assertEquals("IXSCAN", inputStage.getString("stage")),
			() -> Assertions.assertEquals("idx_boardCategory_ownerId", inputStage.getString("indexName"))
		);
	}

	@Test
	void 인덱스_idx_boardCategory_createdAt_사용() {
		for (int i = 0; i < 100; i++) {
			postRepository.save(new FreePost("test", "content", 1L, LocalDateTime.now(), Collections.emptyList()));
		}

		final Map<String, String> doc = new HashMap<>();
		doc.put("createdAt", "test");
		doc.put("boardCategory", "FREE_BOARD");

		Document document = mongoTemplate.getCollection("posts")
			.find(new Document(doc))
			.explain();

		Document queryPlanner = (Document)document.get("queryPlanner");
		Document winningPlan = (Document)queryPlanner.get("winningPlan");
		Document inputStage = (Document)winningPlan.get("inputStage");

		Assertions.assertAll(
			() -> Assertions.assertEquals("IXSCAN", inputStage.getString("stage")),
			() -> Assertions.assertEquals("idx_boardCategory_createdAt", inputStage.getString("indexName"))
		);
	}

	@Test
	void 인덱스_questionId_사용() {
		for (int i = 0; i < 100; i++) {
			postRepository.save(new FreePost("test", "content", 1L, LocalDateTime.now(), Collections.emptyList()));
		}

		Document document = mongoTemplate.getCollection("posts")
			.find(new Document("questionId", "test"))
			.explain();

		Document queryPlanner = (Document)document.get("queryPlanner");
		Document winningPlan = (Document)queryPlanner.get("winningPlan");
		Document inputStage = (Document)winningPlan.get("inputStage");

		Assertions.assertAll(
			() -> Assertions.assertEquals("IXSCAN", inputStage.getString("stage")),
			() -> Assertions.assertEquals("questionId", inputStage.getString("indexName"))
		);
	}

	@Test
	void 인덱스_purchaseId_사용() {
		for (int i = 0; i < 100; i++) {
			postRepository.save(new FreePost("test", "content", 1L, LocalDateTime.now(), Collections.emptyList()));
		}

		Document document = mongoTemplate.getCollection("posts")
			.find(new Document("purchaseId", "test"))
			.explain();

		Document queryPlanner = (Document)document.get("queryPlanner");
		Document winningPlan = (Document)queryPlanner.get("winningPlan");
		Document inputStage = (Document)winningPlan.get("inputStage");

		Assertions.assertAll(
			() -> Assertions.assertEquals("IXSCAN", inputStage.getString("stage")),
			() -> Assertions.assertEquals("purchaseId", inputStage.getString("indexName"))
		);
	}

	@Test
	void 이미지_교체시_사용되지않는_이미지_리턴() {
		final Post post = new FreePost("test", "content", 1L, LocalDateTime.now(), List.of("1", "2", "3"));

		final List<String> prevImages = post.resetImages(List.of("2", "3", "4"));

		// 삭제되는 이미지 리스트 확인
		Assertions.assertAll(
			() -> Assertions.assertTrue(prevImages.contains("1")),

			() -> Assertions.assertFalse(prevImages.contains("2")),
			() -> Assertions.assertFalse(prevImages.contains("3"))
		);

		// 제대로 갱신됐는지
		Assertions.assertAll(
			() -> Assertions.assertFalse(post.getImages().contains("1")),

			() -> Assertions.assertTrue(post.getImages().contains("2")),
			() -> Assertions.assertTrue(post.getImages().contains("3")),
			() -> Assertions.assertTrue(post.getImages().contains("4"))
		);
	}
}