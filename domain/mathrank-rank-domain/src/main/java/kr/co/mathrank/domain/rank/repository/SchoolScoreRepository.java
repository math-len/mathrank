package kr.co.mathrank.domain.rank.repository;

import jakarta.persistence.LockModeType;
import kr.co.mathrank.domain.rank.dto.SchoolRankQueryResult;
import kr.co.mathrank.domain.rank.entity.SchoolScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SchoolScoreRepository extends JpaRepository<SchoolScore, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<SchoolScore> findBySchoolCode(String schoolCode);

    @Query("""
SELECT new kr.co.mathrank.domain.rank.dto.SchoolRankQueryResult(
    ss.schoolCode,
    ss.score,
    RANK() OVER (ORDER BY ss.score DESC),
    (SELECT COUNT(s) FROM Solver s WHERE s.schoolCode = ss.schoolCode)
)
FROM SchoolScore ss
ORDER BY ss.score DESC
""")
    List<SchoolRankQueryResult> findSchoolScores(Pageable pageable);
}
