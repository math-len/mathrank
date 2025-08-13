package kr.co.mathrank.domain.problem.single.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.domain.problem.single.exception.CannotFindProblemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class ProblemInfoManager {
	private final ProblemClient client;

	public ProblemQueryResult fetch(@NotNull final Long problemId) {
		try {
			return client.fetchProblemInfo(problemId);
		} catch (RuntimeException e) {
			log.warn("[ProblemInfoManager.fetch] problem is not exist - problemId: {}", problemId, e);
			throw new CannotFindProblemException();
		}
	}

	public SolveResult solve(@NotNull final Long problemId, final List<String> answers) {
		return client.matchAnswer(problemId, answers);
	}
}
