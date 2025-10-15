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

public interface ContentOrderRepository extends JpaRepository<ContentOrder, Long>, ContentOrderQueryRepository {
	@Query("""
SELECT co FROM ContentOrder co
WHERE co.content.id = :contentId AND co.userId = :userId AND co.orderStatus IN :orderStatuses
""")
	@Lock(LockModeType.PESSIMISTIC_READ)
	Optional<ContentOrder> findByContentIdAndUserIdAndOrderStatusForShare(
		@Param("contentId") Long contentId,
		@Param("userId") Long userId,
		@Param("orderStatuses") List<OrderStatus> orderStatuses
	);

	@Query("""
SELECT co FROM ContentOrder co
WHERE co.id = :orderId AND co.orderStatus = :orderStatus
""")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<ContentOrder> findByOrderIdForUpdate(
		@Param("orderId") Long orderId,
		@Param("orderStatus") OrderStatus orderStatus
	);

	@Query("""
SELECT co FROM ContentOrder co
LEFT JOIN FETCH co.content
WHERE co.id = :orderId AND co.userId = :userId
		""")
	Optional<ContentOrder> findByIdWithContent(@Param("orderId") Long contentId);
}
