package kr.co.mathrank.domain.rank.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.rank.dto.SolveLogRegisterCommand;
import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.exception.SolveLogAlreadyRegisteredException;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class SolveLogSaveManager {
	private final SolverRepository solverRepository;

	@Transactional
	@Retryable(
		retryFor = SolveLogAlreadyRegisteredException.class,
		maxAttempts = 2,
		backoff = @Backoff(
			delay = 1000 // 첫 번째 재시도 대기 시간 (1000ms = 11초)
		))
	void save(@NotNull @Valid final SolveLogRegisterCommand command, final Integer score) {
		try {
			final Solver solver = getSolverOrCreate(command.memberId());
			solver.addSolveLog(command.problemId(), command.singleProblemId(), command.success(), score);

			solverRepository.saveAndFlush(solver);
		} catch (DataIntegrityViolationException e) {
			// (1) SolveLog 중복
			//		- 이미 페어가 존재
			// (2) Solve 중복 문제
			// 		- 현재 해당 기능에 대한 트래픽이 크지 않을거라 예상 중
			// 		retry 활용하여 1회 재시도 만으로 해결 시도
			log.warn(
				"[SolveLogSaveManager.save] cannot save solve log, already exist - singleProblemId: {}, problemId: {}, memberId: {}, success: {}",
				command.singleProblemId(), command.problemId(), command.memberId(), command.success());
			throw new SolveLogAlreadyRegisteredException();
		}
	}

	private Solver getSolverOrCreate(final Long memberId) {
		return solverRepository.findByMemberIdForUpdate(memberId)
			.orElseGet(() -> Solver.of(memberId));
	}
}
