package kr.co.mathrank.domain.rank.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.rank.dto.SolveLogRegisterCommand;
import kr.co.mathrank.domain.rank.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SolveLogRegisterService {
	private final ProblemRankScoreManager rankScoreManager;
	private final ProblemClient problemClient;
	private final RankRepository rankRepository;

	private final SolveLogSaveManager solveLogSaveManager;

	public void register(@NotNull @Valid final SolveLogRegisterCommand command) {
		final Integer score = getProblemScore(command.problemId());
		final Long memberScore = solveLogSaveManager.save(command, score);
		saveMemberScore(command.memberId(), memberScore);
	}

	private int getProblemScore(final Long problemId) {
		final ProblemQueryResult result = problemClient.fetchProblemInfo(problemId);
		return rankScoreManager.mapToScore(result.difficulty());
	}

	private void saveMemberScore(final Long memberId, final Long memberScore) {
		rankRepository.set(String.valueOf(memberId), memberScore);
	}
}
