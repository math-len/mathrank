package kr.co.mathrank.domain.rank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.rank.dto.SolverUpdateCommand;
import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.exception.CannotFoundSolverException;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SolverUpdateService {
	private final SolverRepository solverRepository;

	@Transactional
	public void updateSolver(@NotNull @Valid final SolverUpdateCommand command) {
		final Solver solver = solverRepository.findByMemberId(command.memberId())
			.orElseThrow(() -> {
				log.info("[SolverUpdateService.updateSolver] cannot found solver - memberId: {}", command.memberId());
				return new CannotFoundSolverException("사용자를 찾을 수 없습니다.");
			});

		solver.setSchoolCode(command.schoolCode());
	}
}
