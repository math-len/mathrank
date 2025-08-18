package kr.co.mathrank.domain.problem.assessment.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentItemCountNotMatchException;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentItemTotalScoreException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
class AssessmentItems {
	private static final int TOTAL_SCORE = 100; // 점수의 총합은 100점

	@OneToMany(mappedBy = "assessment", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private final List<AssessmentItem> assessmentItems = new ArrayList<>();

	public void updateAssessmentItems(final List<Long> problemIds, final List<Integer> scores, final Assessment assessment) {
		this.assessmentItems.clear();

		validateSize(problemIds, scores);
		validateTotalScore(scores);

		for (int i = 0; i < problemIds.size(); i++) {
			assessmentItems.add(AssessmentItem.of(i + 1, problemIds.get(i), scores.get(i), assessment));
		}
	}

	private void validateSize(List<Long> problemIds, List<Integer> scores) {
		if (problemIds.size() != scores.size()) {
			log.info("[AssessmentItems] Problem IDs and scores size mismatch - problemIds.size: {}, scores.size: {}", problemIds.size(), scores.size());
			throw new AssessmentItemCountNotMatchException();
		}
	}

	private void validateTotalScore(final List<Integer> scores) {
		final int currentTotalScore = scores.stream()
			.mapToInt(i -> i)
			.sum();

		if (currentTotalScore != TOTAL_SCORE) {
			log.info("[AssessmentItems.validateTotalScore] Total score is incorrect - total: {}", currentTotalScore);
			throw new AssessmentItemTotalScoreException();
		}
	}
}
