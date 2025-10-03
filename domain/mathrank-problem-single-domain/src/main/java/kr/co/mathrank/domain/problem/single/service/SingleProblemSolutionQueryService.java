package kr.co.mathrank.domain.problem.single.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolutionQuery;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolutionQueryResult;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.exception.CannotAccessSolutionException;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemSolutionQueryService {
	private final SingleProblemRepository singleProblemRepository;
	private final ProblemInfoManager problemInfoManager;

	public SingleProblemSolutionQueryResult getSolution(
		@NotNull @Valid final SingleProblemSolutionQuery query
	) {
		final SingleProblem singleProblem = singleProblemRepository.findByIdAndChallengerMemberId(
				query.singleProblemId(), query.requestMemberId())
			.orElseThrow(() -> {
				log.info(
					"[SingleProblemSolutionQueryService.getSolution] cannot find single problem solved by member - singleProblemId: {}, requestMemberId: {}",
					query.singleProblemId(), query.requestMemberId());
				return new CannotAccessSolutionException();
			});
		final ProblemQueryResult problemQueryResult = problemInfoManager.fetch(singleProblem.getProblemId());
		return SingleProblemSolutionQueryResult.from(problemQueryResult);
	}
}
