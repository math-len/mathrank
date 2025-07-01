package kr.co.mathrank.domain.board.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.board.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Query("SELECT c FROM Comment c LEFT JOIN FETCH c.images WHERE c.id = :commentId")
	Optional<Comment> findWithImages(@Param("commentId") Long commentId);

	@Query("SELECT c FROM Comment c LEFT JOIN FETCH c.images WHERE c.postId = :postId ORDER BY c.createdAt ASC")
	List<Comment> queryAllByPostIdSortedByCreatedAt(Long postId);
}
