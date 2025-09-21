package kr.co.mathrank.domain.board.constraints;

import jakarta.validation.groups.Default;

public class ValidationGroups {
	public interface SingleProblemPostGroup extends Default {}
	public interface AssessmentPostGroup extends Default {}
	public interface ContestPostGroup extends Default {}
	public interface FreePostGroup extends Default {}
}
