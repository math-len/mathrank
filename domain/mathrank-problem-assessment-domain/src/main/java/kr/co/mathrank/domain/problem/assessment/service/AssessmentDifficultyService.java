package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.core.Difficulty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AssessmentDifficultyService {
	private final AssessmentRepository assessmentRepository;
	private final ProblemQueryManager problemQueryManager;

	public void updateToAverageDifficulty(@NotNull final Long assessmentId) {
		final Assessment assessment = assessmentRepository.findWithItems(assessmentId)
			.orElseThrow(() -> {
				log.info("[AssessmentDifficultyService.updateToAverageDifficulty] assessment with id not found - assessmentId: {}", assessmentId);
				return new NoSuchAssessmentException();
			});

		final List<Difficulty> difficulties = getProblemInfos(assessment.getAssessmentItems());
		final Difficulty averageDifficulty = getAverageDifficulty(difficulties);

		assessment.setDifficulty(averageDifficulty);
		assessmentRepository.save(assessment);
		log.info("[AssessmentDifficultyService.updateToAverageDifficulty] assessment saved - assessmentId: {}, difficulty: {}", assessmentId, averageDifficulty);
	}

	private List<Difficulty> getProblemInfos(final List<AssessmentItem> assessmentItems) {
		return assessmentItems.stream()
			.map(assessmentItem -> problemQueryManager.getProblemInfo(assessmentItem.getProblemId()))
			.map(ProblemQueryResult::difficulty)
			.toList();
	}

	private Difficulty getAverageDifficulty(final List<Difficulty> difficulties) {
		// 전체 난이도의 총합을 구하고, 평균을 계산
		final double averagePriority = difficulties.stream()
			.mapToInt(Difficulty::getPriority)
			.average()
			.orElse(0.0d);

		// 평균값에 가장 가까운 난이도를 찾기
		return fromPriority(averagePriority);
	}

	/**
	 * 가장 가까운 난이도를 리턴합니다.
	 * @param averagePriority
	 * @return
	 */
	private Difficulty fromPriority(final double averagePriority) {
		Difficulty closestDifficulty = null;
		double minDifference = Double.MAX_VALUE;

		for (Difficulty difficulty : Difficulty.values()) {
			double difference = Math.abs(difficulty.getPriority() - averagePriority);
			if (difference < minDifference) {
				minDifference = difference;
				closestDifficulty = difficulty;
			}
		}

		return closestDifficulty;
	}
}
