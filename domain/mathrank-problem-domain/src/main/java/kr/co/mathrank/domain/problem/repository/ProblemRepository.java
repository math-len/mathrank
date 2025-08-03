package kr.co.mathrank.domain.problem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.problem.entity.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemQueryRepository {
	@Query("SELECT p FROM Problem p LEFT JOIN FETCH p.answers WHERE p.id = :problemId")
	Optional<Problem> findProblemByIdWithAnswer(@Param("problemId") final Long problemId);
}
