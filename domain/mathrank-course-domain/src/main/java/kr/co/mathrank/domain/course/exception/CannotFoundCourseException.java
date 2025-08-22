package kr.co.mathrank.domain.course.exception;

public class CannotFoundCourseException extends CourseException {
	private static final String FORMAT = "존재하지 않는 과정: %s";

	public CannotFoundCourseException(final String path) {
		super(2001, "존재하지 않는 과정: " + FORMAT.formatted(path));
	}
}
