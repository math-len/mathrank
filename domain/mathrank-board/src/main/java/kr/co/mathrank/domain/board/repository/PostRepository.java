package kr.co.mathrank.domain.board.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import kr.co.mathrank.domain.board.entity.BoardCategory;
import kr.co.mathrank.domain.board.entity.Post;

public interface PostRepository extends MongoRepository<Post, String> {
	List<Post> findAllByBoardCategoryAndCreatedAtIsBefore(BoardCategory boardCategory, LocalDateTime createdAtBefore,
		Pageable pageable);

	List<Post> findAllByBoardCategoryAndOwnerId(BoardCategory boardCategory, Long ownerId, Pageable pageable);
}
