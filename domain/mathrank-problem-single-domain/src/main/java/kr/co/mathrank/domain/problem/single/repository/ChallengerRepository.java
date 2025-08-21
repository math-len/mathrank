package kr.co.mathrank.domain.problem.single.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.problem.single.entity.Challenger;

public interface ChallengerRepository extends JpaRepository<Challenger, Long> {
	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT c FROM Challenger c LEFT JOIN FETCH c.challengeLogs WHERE c.singleProblem.id = :singleProblemId AND c.memberId = :memberId")
	Optional<Challenger> findByMemberIdAndSingleProblemIdForShare(@Param("memberId") Long memberId, @Param("singleProblemId") Long singleProblemId);
}
