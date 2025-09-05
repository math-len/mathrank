package kr.co.mathrank.domain.rank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.rank.entity.Solver;

public interface SolverRepository extends JpaRepository<Solver, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Solver s WHERE s.memberId = :memberId")
	Optional<Solver> findByMemberIdForUpdate(@Param("memberId") Long memberId);

	@Query("SELECT s FROM Solver s LEFT JOIN FETCH s.solveLogs WHERE s.memberId = :memberId")
	Optional<Solver> findByMemberId(Long memberId);

	// 회원의 랭크를 조회하는 쿼리 (점수 기준 내림차순으로 랭크 계산)
	@Query("""
SELECT COUNT(s) FROM Solver s
WHERE s.score > (
	SELECT solver.score 
	FROM Solver solver 
	WHERE solver.memberId = :memberId
)
""")
	Long findRankByMemberId(@Param("memberId") Long memberId);

	@Query("""
SELECT s.score 
FROM Solver s 
WHERE s.memberId = :memberId
""")
	Long findScoreByMemberId(@Param("memberId") Long memberId);

	@Query("""
SELECT s FROM Solver s
""")
	List<Solver> findAllSolversDescendingScores(Pageable pageable);
}
