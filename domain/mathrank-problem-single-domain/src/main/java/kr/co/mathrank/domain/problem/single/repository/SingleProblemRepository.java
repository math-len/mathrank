package kr.co.mathrank.domain.problem.single.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.problem.single.entity.SingleProblem;

public interface SingleProblemRepository extends JpaRepository<SingleProblem, Long> {
	Optional<SingleProblem> findByProblemId(Long problemId);
}
