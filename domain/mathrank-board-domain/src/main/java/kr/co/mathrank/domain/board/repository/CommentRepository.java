package kr.co.mathrank.domain.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM Comment c LEFT JOIN FETCH c.post WHERE c.id = :commentId")
	Optional<Comment> findCommentWithPostForUpdate(@Param("commentId") final Long commentId);
}
