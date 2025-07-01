package kr.co.mathrank.domain.board.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.board.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
