package kr.co.mathrank.domain.problem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.problem.entity.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemQueryRepository {
}
