package kr.co.mathrank.domain.problem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;

public interface CourseRepository extends JpaRepository<Course, Path> {
}
