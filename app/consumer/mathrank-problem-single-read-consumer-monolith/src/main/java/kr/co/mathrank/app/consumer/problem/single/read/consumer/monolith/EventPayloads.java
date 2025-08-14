package kr.co.mathrank.app.consumer.problem.single.read.consumer.monolith;

import java.time.LocalDateTime;

import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemAttemptStatsUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelRegisterCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;

public class EventPayloads {
	record ProblemUpdatedEventPayload(
		Long problemId,
		String coursePath,
		String problemImage,
		AnswerType answerType,
		Difficulty difficulty,
		LocalDateTime updatedAt,
		Integer year,
		String schoolCode,
		String location,
		String memo
	) implements EventPayload {
		SingleProblemReadModelUpdateCommand toCommand() {
			return new SingleProblemReadModelUpdateCommand(
				problemId,
				coursePath,
				problemImage,
				answerType,
				difficulty,
				updatedAt
			);
		}
	}

	record SingleProblemSolvedEventPayload(
		Long singleProblemId,
		Long problemId,
		Long memberId,
		Boolean success,
		Long firstTrySuccessCount,
		Long totalAttemptedCount,
		Long attemptedUserDistinctCount
	) implements EventPayload {
		SingleProblemAttemptStatsUpdateCommand toCommand() {
			return new SingleProblemAttemptStatsUpdateCommand(
				singleProblemId,
				firstTrySuccessCount,
				totalAttemptedCount,
				attemptedUserDistinctCount
			);
		}
	}

	record SingleProblemRegisteredEventPayload(
		Long singleProblemId,
		Long problemId,
		String coursePath,
		String problemImage,
		AnswerType answerType,
		Difficulty difficulty,
		LocalDateTime registeredAt,
		Long firstTrySuccessCount, // 첫 시도에 성공한 횟수
		Long totalAttemptedCount, // 문제를 풀려고 시도한 총 횟수
		Long attemptedUserDistinctCount // 해당 문제를 풀려고 한 사용자 수
	) implements EventPayload {
		SingleProblemReadModelRegisterCommand toCommand() {
			return new SingleProblemReadModelRegisterCommand(
				singleProblemId,
				problemId,
				problemImage,
				answerType,
				difficulty,
				coursePath,
				registeredAt
			);
		}
	}
}
