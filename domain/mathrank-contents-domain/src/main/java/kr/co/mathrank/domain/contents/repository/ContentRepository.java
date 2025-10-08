package kr.co.mathrank.domain.contents.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.contents.entity.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
