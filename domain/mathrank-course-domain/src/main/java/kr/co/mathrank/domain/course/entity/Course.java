package kr.co.mathrank.domain.course.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
	@EmbeddedId
	private Path path = new Path();

	private String courseName;

	public static Course of(final String courseName, final Path path) {
		final Course course = new Course();
		course.path = path;
		course.courseName = courseName;

		return course;
	}
}