package kr.co.mathrank.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.board.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
