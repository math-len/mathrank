package kr.co.mathrank.domain.problem.single.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRegisterCommand;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveCommand;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveResult;
import kr.co.mathrank.domain.problem.single.entity.ChallengeLog;
import kr.co.mathrank.domain.problem.single.entity.SingleProblem;
import kr.co.mathrank.domain.problem.single.exception.AlreadyRegisteredProblemException;
import kr.co.mathrank.domain.problem.single.exception.CannotFindSingleProblemException;
import kr.co.mathrank.domain.problem.single.exception.CannotRegisterWithThisRoleException;
import kr.co.mathrank.domain.problem.single.repository.SingleProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemService {
	private final SingleProblemRepository singleProblemRepository;

	private final ChallengeLogSaveManager challengeLogSaveManager;

	private final ProblemInfoManager problemInfoManager;
	private final SingleProblemRegisterManager singleProblemRegisterManager;

	/**
	 * 문제를 개별문제로 등록하기 위한 API입니다.
	 * @param command
	 */
	public Long register(@NotNull @Valid final SingleProblemRegisterCommand command) {
		// 관리자만 문제를 개별문제로 등록 가능하다
		if (command.role() != Role.ADMIN) {
			log.warn("[SingleProblemService.register] cannot register single problem with this user - userId:{}, userRole: {}", command.memberId(), command.role());
			throw new CannotRegisterWithThisRoleException();
		}

		// 존재하는 problem인지 확인한다.
		final ProblemQueryResult result = problemInfoManager.fetch(command.problemId());
		final SingleProblem singleProblem = SingleProblem.of(command.problemId(), command.singleProblemName(), command.memberId());

		singleProblemRegisterManager.register(singleProblem, result);
		log.info("[SingleProblemService.register] single problem registered - singleProblemId: {}, problemId: {}", singleProblem.getId(), singleProblem.getProblemId());
		return singleProblem.getId();
	}

	/**
	 * 개별문제의 채점 기록을 저장하는 API입니다.
	 * @param command
	 */
	public SingleProblemSolveResult solve(@NotNull @Valid final SingleProblemSolveCommand command) {
		final SingleProblem singleProblem = singleProblemRepository.findById(command.singleProblemId())
			.orElseThrow(() -> {
				log.warn("[SingleProblemService.solve] cannot find single problem with id: {}",
					command.singleProblemId());
				return new CannotFindSingleProblemException();
			});
		// 채점 서비스 호출.
		// 외부 호출임에 따라, 트랜잭션 제거
		final SolveResult solveResult = problemInfoManager.solve(singleProblem.getProblemId(), command.answers());
		final SingleProblemSolveResult singleProblemSolveResult = SingleProblemSolveResult.from(solveResult);

		final Long challengeLogId = challengeLogSaveManager.saveLog(singleProblem.getId(), command.memberId(), singleProblemSolveResult, command.duration());
		return SingleProblemSolveResult.from(solveResult, challengeLogId);
	}
}
