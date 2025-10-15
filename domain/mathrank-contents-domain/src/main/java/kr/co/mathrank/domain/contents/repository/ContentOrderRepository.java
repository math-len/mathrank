package kr.co.mathrank.domain.contents.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.contents.entity.ContentOrder;
import kr.co.mathrank.domain.contents.entity.OrderStatus;

public interface ContentOrderRepository extends JpaRepository<ContentOrder, Long> {
	@Query("""
SELECT co FROM ContentOrder co
WHERE co.content.id = :id AND co.userId = :userId AND co.orderStatus IN :orderStatuses
""")
	@Lock(LockModeType.PESSIMISTIC_READ)
	Optional<ContentOrder> findByContentIdAndUserIdAndOrderStatusForShare(
		@Param("id") Long contentId,
		@Param("userId") Long userId,
		@Param("orderStatuses") List<OrderStatus> orderStatuses
	);
}
