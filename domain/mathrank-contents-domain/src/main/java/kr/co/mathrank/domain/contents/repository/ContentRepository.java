package kr.co.mathrank.domain.contents.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.contents.entity.Content;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentQueryRepository {
	@Query("""
SELECT c FROM Content c
LEFT JOIN FETCH c.contentUsers user
WHERE c.id = :contentId AND user.userId = :userId
""")
	Optional<Content> findContentPurchasedByUser(@Param("contentId") Long contentId, @Param("userId") Long userId);
}
