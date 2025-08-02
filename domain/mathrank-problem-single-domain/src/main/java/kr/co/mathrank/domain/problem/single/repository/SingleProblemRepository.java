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

	@Query("SELECT COUNT(cl) > 0 FROM ChallengeLog cl WHERE cl.singleProblem.id = :singleProblemId AND cl.memberId = :challengerMemberId")
	Boolean getAlreadySolved(@Param("singleProblemId") Long singleProblemId, @Param("challengerMemberId") Long challengerMemberId);
}
