package kr.co.mathrank.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
