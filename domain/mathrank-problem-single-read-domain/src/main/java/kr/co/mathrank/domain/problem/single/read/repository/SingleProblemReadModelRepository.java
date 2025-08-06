package kr.co.mathrank.domain.problem.single.read.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;

public interface SingleProblemReadModelRepository extends JpaRepository<SingleProblemReadModel, Long> {
}
