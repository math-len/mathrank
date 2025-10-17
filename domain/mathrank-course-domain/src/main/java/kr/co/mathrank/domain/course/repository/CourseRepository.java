package kr.co.mathrank.domain.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.mathrank.domain.course.entity.Course;
import kr.co.mathrank.domain.course.entity.Path;

public interface CourseRepository extends JpaRepository<Course, Path> {
	@Query("SELECT c FROM Course c WHERE c.path.path LIKE CONCAT(:path, '%')")
	List<Course> queryCourseStartsWith(@Param("path") final String path, Pageable pageable);

	@Query("SELECT c FROM Course c WHERE c.path.path LIKE CONCAT(:path, '%') AND LENGTH(c.path.path) = :depthLength")
	List<Course> queryChildes(@Param("path") final String path, @Param("depthLength") final Integer targetPathLength);

	@Query("SELECT c FROM Course c WHERE c.courseName = :courseName AND LENGTH(c.path.path) = :pathLength")
	Optional<Course> findByCourseNameAndPathLength(@Param("courseName") final String courseName, @Param("pathLength") final Integer pathLength);

	@Query("SELECT c FROM Course c WHERE c.courseName = :courseName AND c.path.path LIKE CONCAT(:path, '__')")
	Optional<Course> findByCourseNameAndPathStartsWith(@Param("courseName") String courseName, @Param("path") String path);

	List<Course> findAllByCourseName(String courseName);

	Optional<Course> findByPath(Path path);

	List<Course> findAllByPathIn(List<Path> paths);
}

