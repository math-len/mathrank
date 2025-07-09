package kr.co.mathrank.domain.problem.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.problem.entity.Course;
import kr.co.mathrank.domain.problem.entity.Path;

public interface CourseRepository extends JpaRepository<Course, Path> {
	@Query("SELECT c FROM Course c WHERE c.path.path LIKE CONCAT(:path, '%')")
	List<Course> queryCourseStartsWith(@Param("path") final String path, Pageable pageable);
}
