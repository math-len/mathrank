package kr.co.mathrank.domain.board.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import kr.co.mathrank.domain.board.entity.BoardCategory;
import kr.co.mathrank.domain.board.entity.Post;

public interface PostRepository extends MongoRepository<Post, String> {
	List<Post> findAllByBoardCategoryAndCreatedAtIsBeforeAndDeletedIsFalse(BoardCategory boardCategory,
		LocalDateTime createdAtBefore, Pageable pageable);

	List<Post> findAllByBoardCategoryAndOwnerIdAndDeletedIsFalse(BoardCategory boardCategory, Long ownerId,
		Pageable pageable);

	@Query("{ 'outbox': { '$exists': true } }")
	List<Post> findAllContainsOutbox();

	Optional<Post> findByIdAndDeletedIsFalse(String id);
}
