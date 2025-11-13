package kr.co.mathrank.domain.rank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.rank.entity.Solver;
import kr.co.mathrank.domain.rank.exception.CannotFoundSolverException;
import kr.co.mathrank.domain.rank.repository.SolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SolverDeleteService {
	private final SolverRepository solverRepository;

	@Transactional
	public void delete(@NotNull @Valid final Long memberId) {
		final Solver solver = solverRepository.findById(memberId)
			.orElseThrow(() -> {
				log.info("[SolverDeleteService.delete] cannot found solver - memberId: {}", memberId);
				return new CannotFoundSolverException("사용자를 찾을 수 없습니다.");
			});

		solverRepository.delete(solver);
		log.info("[SolverDeleteService.delete] solver deleted - memberId: {}", memberId);
	}
}
