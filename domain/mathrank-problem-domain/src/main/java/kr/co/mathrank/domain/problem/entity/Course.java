package kr.co.mathrank.domain.problem.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString(exclude = "problems")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
	@EmbeddedId
	private Path path = new Path();

	private String courseName;

	@OneToMany(mappedBy = "course")
	private final List<Problem> problems = new ArrayList<>();

	public static Course of(final String courseName, final Path path) {
		final Course course = new Course();
		course.path = path;
		course.courseName = courseName;

		return course;
	}
}
