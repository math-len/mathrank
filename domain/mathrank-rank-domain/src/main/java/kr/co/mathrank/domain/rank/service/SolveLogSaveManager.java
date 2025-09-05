package kr.co.mathrank.domain.rank.service;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.RecoverableDataAccessException;
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
import kr.co.mathrank.domain.rank.exception.SolverAlreadyExistException;
import kr.co.mathrank.domain.rank.repository.SolveLogRepository;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class SolveLogSaveManager {
	private final SolverRepository solverRepository;
	private final SolveLogRepository solveLogRepository;

	@Transactional
	@Retryable(
		retryFor = SolverAlreadyExistException.class,
		maxAttempts = 2,
		backoff = @Backoff(
			delay = 1000 // 첫 번째 재시도 대기 시간 (1000ms = 1초)
		))
	void save(@NotNull @Valid final SolveLogRegisterCommand command, final Integer score) {
		try {
			final Solver solver = getSolverOrCreate(command.memberId());

			// 자식의 유니크 제약조건 예외를 발견하고 롤백을 위함
			// 		- 전파 속성으로 영속화할 때, 유니크 제약조건 위반으로 인한 롤백을 예상했으나, 실제론 예외를 던지지 않았고 롤백 X, 부모엔티티만 영속화되는 버그 발견
			//		- 아래와 같이 직접적으로 영속화 시 예외를 정상적으로 던짐. => 롤백을 위함
			solveLogRepository.save(
				solver.addSolveLog(command.problemId(), command.singleProblemId(), command.success(), score));

			solverRepository.saveAndFlush(solver);
		} catch (DataIntegrityViolationException e) {
			// (1) SolveLog 중복
			//		- 이미 페어가 존재
			//		- 재시도 X 예외 처리
			log.info(
				"[SolveLogSaveManager.save] cannot save solve log, already exist - singleProblemId: {}, problemId: {}, memberId: {}, success: {}",
				command.singleProblemId(), command.problemId(), command.memberId(), command.success());
			throw new SolveLogAlreadyRegisteredException();
		} catch (CannotAcquireLockException e) {
			// (2) Solver 중복 문제
			// 		- 동시 삽입 시도로 인해 gap lock이 발생, 데드락.
			// 		- 현재 해당 기능에 대한 트래픽이 크지 않을거라 예상 중
			// 		retry 활용하여 1회 재시도 만으로 해결
			log.warn("[SolveLogSaveManager.save] solver already exist. it will be retry - memberId: {}", command.memberId(), e);
			throw new SolverAlreadyExistException();
		}
	}

	private Solver getSolverOrCreate(final Long memberId) {
		return solverRepository.findByMemberIdForUpdate(memberId)
			.orElseGet(() -> Solver.of(memberId));
	}
}
