package kr.co.mathrank.domain.board.viewcount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.board.viewcount.entity.ViewCount;

public interface ViewCountBackupRepository extends JpaRepository<ViewCount, Long> {
	@Query("SELECT vc.viewCount FROM ViewCount vc WHERE vc.postId = :postId")
	Long getViewCountByPostId(@Param("postId") Long postId);
}
