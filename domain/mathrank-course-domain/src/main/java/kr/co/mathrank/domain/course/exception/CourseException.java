package kr.co.mathrank.domain.course.exception;

import kr.co.mathrank.common.exception.MathRankException;

public class CourseException extends MathRankException {
	CourseException(final int code, final String message) {
		super(code, message);
	}
}
