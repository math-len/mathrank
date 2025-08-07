package kr.co.mathrank.domain.problem.single.read.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class SingleProblemReadModelDeleteService {
	private final SingleProblemReadModelRepository singleProblemReadModelRepository;

	public void deleteByProblemId(@NotNull final Long problemId) {
		singleProblemReadModelRepository.deleteByProblemId(problemId);
		log.info("[SingleProblemReadModelDeleteService.delete] read model deleted - problemId: {}", problemId);
	}

	public void deleteBySingleProblemId(@NotNull final Long singleProblemId) {
		singleProblemReadModelRepository.deleteById(singleProblemId);
		log.info("[SingleProblemReadModelDeleteService.delete] read model deleted - singleProblemId: {}", singleProblemId);
	}
}
