package kr.co.mathrank.domain.problem.single.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;

public interface SingleProblemRepository extends JpaRepository<SingleProblem, Long> {
	@Query("SELECT sp FROM SingleProblem sp WHERE sp.id = :singleProblemId")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<SingleProblem> findByIdForUpdate(@Param("singleProblemId") Long singleProblemId);

	@Query("""
SELECT sp FROM SingleProblem sp 
LEFT JOIN FETCH sp.challengers c
WHERE sp.id = :singleProblemId
""")
	Optional<SingleProblem> findWithChallengers(@Param("singleProblemId") Long singleProblemId);
}
