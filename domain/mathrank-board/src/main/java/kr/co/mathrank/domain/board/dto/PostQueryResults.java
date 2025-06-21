package kr.co.mathrank.domain.board.dto;

import java.util.List;

public record PostQueryResults(
	List<PostQueryResult> results
) {
}
