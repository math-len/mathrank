package kr.co.mathrank.domain.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.board.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM Post p WHERE p.id = :postId")
	Optional<Post> findByIdForUpdate(@Param("postId") final Long postId);
}
