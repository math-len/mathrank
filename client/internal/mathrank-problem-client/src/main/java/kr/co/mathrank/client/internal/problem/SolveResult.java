package kr.co.mathrank.client.internal.problem;

import java.util.List;
import java.util.Set;

public record SolveResult(
	Boolean success,
	Set<String> realAnswer,
	List<String> submittedAnswer
) {
}
