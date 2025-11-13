package kr.co.mathrank.domain.rank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
		solverRepository.findById(memberId)
			.ifPresentOrElse(solver -> {
				solverRepository.delete(solver);
				log.info("[SolverDeleteService.delete] solver deleted - memberId: {}", memberId);
			}, () -> {
				log.info("[SolverDeleteService.delete] cannot found solver - memberId: {}", memberId);
			});
	}
}
