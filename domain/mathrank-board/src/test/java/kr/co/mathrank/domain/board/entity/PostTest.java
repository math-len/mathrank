package kr.co.mathrank.domain.board.entity;

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
			postRepository.save(new Post("test", "content", 1L, Collections.emptyList(), BoardCategory.FREE_BOARD));
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
			postRepository.save(new Post("test", "content", 1L, Collections.emptyList(), BoardCategory.FREE_BOARD));
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
			postRepository.save(new Post("test", "content", 1L, Collections.emptyList(), BoardCategory.FREE_BOARD));
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
			postRepository.save(new Post("test", "content", 1L, Collections.emptyList(), BoardCategory.FREE_BOARD));
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
}