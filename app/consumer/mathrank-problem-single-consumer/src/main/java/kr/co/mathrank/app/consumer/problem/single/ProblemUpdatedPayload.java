package kr.co.mathrank.app.consumer.problem.single;

import java.time.LocalDateTime;

import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;

public record ProblemUpdatedPayload(
	Long problemId,
	String coursePath,
	String problemImage,
	AnswerType answerType,
	Difficulty difficulty,
	LocalDateTime updatedAt
) implements EventPayload {
	public SingleProblemReadModelUpdateCommand toCommand() {
		return new SingleProblemReadModelUpdateCommand(problemId, coursePath, problemImage, answerType, difficulty,
			updatedAt);
	}
}
