package kr.co.mathrank.domain.problem.single.read.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelUpdateCommand;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.exception.CannotFoundProblemException;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 문제 읽기 모델(SingleProblemReadModel)을 업데이트하는 서비스입니다.
 *
 * 해당 서비스는 주로 문제 이미지, 정답 유형, 난이도, 코스 경로 등의 정보를 갱신합니다.
 *
 * idempotency를 보장하기 위해, 요청된 정보의 업데이트 시간이 기존보다 최신일 경우에만 업데이트를 수행합니다.
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemUpdateService {
	private final SingleProblemReadModelRepository singleProblemReadModelRepository;

	/**
	 * 단일 문제 읽기 모델 정보를 업데이트합니다.
	 *
	 * @param command 업데이트에 필요한 문제 정보가 담긴 커맨드 객체
	 * @throws CannotFoundProblemException 해당 problemId에 대응되는 읽기 모델이 존재하지 않을 경우 발생
	 */
	@Transactional
	public void updateProblemInfo(@NotNull @Valid final SingleProblemReadModelUpdateCommand command) {
		final SingleProblemReadModel model = singleProblemReadModelRepository.findByProblemIdForUpdate(command.problemId())
			.orElseThrow(() -> {
				log.warn("[SingleProblemUpdateService.updateProblemInfo] cannot find problemId: {}", command.problemId());
				return new CannotFoundProblemException();
			});

		// idempotency 보장을 위해, 커맨드의 updatedAt이 더 최신일 때만 업데이트 수행
		if (command.updatedAt().isAfter(model.getUpdatedAt())) {
			model.setProblemImage(command.problemImage());
			model.setDifficulty(command.difficulty());
			model.setCoursePath(command.coursePath());
			model.setAnswerType(command.answerType());
			log.info("[SingleProblemUpdateService.updateProblemInfo] read model updated: {}", command);
			return;
		}
		log.info("[SingleProblemUpdateService.updateProblemInfo] read model not updated: {}", command);
	}
}
