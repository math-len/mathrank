package kr.co.mathrank.domain.problem.assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
}
