package kr.co.mathrank.app.consumer.problem.single.read.consumer.monolith;

import java.time.LocalDateTime;

import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemAttemptStatsUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelNameUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelRegisterCommand;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;

public class EventPayloads {
	record ProblemUpdatedEventPayload(
		Long problemId,
		String coursePath,
		String problemImage,
		AnswerType answerType,
		Difficulty difficulty,
		PastProblem pastProblem,
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
				location,
				schoolCode,
				answerType,
				difficulty,
				pastProblem,
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
				memberId,
				success,
				firstTrySuccessCount,
				totalAttemptedCount,
				attemptedUserDistinctCount
			);
		}
	}

	record SingleProblemRegisteredEventPayload(
		Long singleProblemId,
		Long problemId,
		String singleProblemName,
		String coursePath,
		String problemImage,
		String location,
		String schoolCode,
		AnswerType answerType,
		Difficulty difficulty,
		LocalDateTime registeredAt,
		PastProblem pastProblem,
		Long firstTrySuccessCount, // 첫 시도에 성공한 횟수
		Long totalAttemptedCount, // 문제를 풀려고 시도한 총 횟수
		Long attemptedUserDistinctCount // 해당 문제를 풀려고 한 사용자 수
	) implements EventPayload {
		SingleProblemReadModelRegisterCommand toCommand() {
			return new SingleProblemReadModelRegisterCommand(
				singleProblemId,
				problemId,
				singleProblemName,
				location,
				schoolCode,
				problemImage,
				answerType,
				difficulty,
				pastProblem,
				coursePath,
				registeredAt
			);
		}
	}

	record SingleProblemDeletedEvent(
		Long singleProblemId,
		String singleProblemName,
		Long problemId,
		Long memberId
	) implements EventPayload {

	}

	record SingleProblemNameUpdatedEventPayload(
		Long singleProblemId,
		String singleProblemName,
		Long problemId,
		Long memberId
	) implements EventPayload {
		public SingleProblemReadModelNameUpdateCommand toCommand() {
			return new SingleProblemReadModelNameUpdateCommand(
				singleProblemId,
				singleProblemName
			);
		}
	}

	record ProblemDeletedEvent(
		Long id,
		Long memberId,
		String problemImage,
		String solutionImage,
		Difficulty difficulty,
		AnswerType type,
		PastProblem pastProblem,
		String coursePath,
		String schoolCode,
		String location,
		Integer years,
		String solutionVideoLink
	) implements EventPayload {
	}
}
