package kr.co.mathrank.domain.single.problem.rank.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.single.problem.rank.dto.SolveLogRegisterCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SolveLogRegisterService {
	private final ProblemRankScoreManager rankScoreManager;
	private final ProblemClient problemClient;

	private final SolveLogSaveManager solveLogSaveManager;

	public void register(@NotNull @Valid final SolveLogRegisterCommand command) {
		final Integer score = getScore(command.problemId());
		solveLogSaveManager.save(command, score);
	}

	private int getScore(final Long problemId) {
		final ProblemQueryResult result = problemClient.fetchProblemInfo(problemId);
		return rankScoreManager.mapToScore(result.difficulty());
	}
}
