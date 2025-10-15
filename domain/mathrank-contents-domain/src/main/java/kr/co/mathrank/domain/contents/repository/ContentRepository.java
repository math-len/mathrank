package kr.co.mathrank.domain.contents.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.contents.entity.Content;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentQueryRepository {
	@Query("""
SELECT c FROM Content c
LEFT JOIN FETCH c.contentOrders
WHERE c.id = :contentId
""")
	Optional<Content> findContentWithPurchasedUsers(@Param("contentId") Long contentId);

	@Query("""
SELECT c FROM Content c
WHERE c.id = :contentId
""")
	@Lock(LockModeType.PESSIMISTIC_READ)
	Optional<Content> findByIdForShare(@Param("contentId") Long contentId);
}
