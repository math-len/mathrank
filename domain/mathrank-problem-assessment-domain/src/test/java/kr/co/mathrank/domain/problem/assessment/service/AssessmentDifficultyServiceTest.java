package kr.co.mathrank.domain.problem.assessment.service;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.core.Difficulty;

@SpringBootTest(properties = """
client.problem.port=20102
client.problem.host=http://localhost
""")
class AssessmentDifficultyServiceTest {
	@Autowired
	private AssessmentRepository assessmentRepository;
	@Autowired
	private AssessmentDifficultyService difficultyService;
	@MockitoBean
	private ProblemQueryManager problemQueryManager;

	@Test
	void 평균값으로_배정된다() {
		final Long problemId1 = 1L;
		final Long problemId2 = 2L;

		Mockito.when(problemQueryManager.getProblemInfo(problemId1)).thenReturn(createProblemQueryResult(Difficulty.LOW));
		Mockito.when(problemQueryManager.getProblemInfo(problemId2)).thenReturn(createProblemQueryResult(Difficulty.MID));

		final Assessment assessment = Assessment.unlimited(1L, "test", Duration.ofMinutes(10));
		assessment.replaceItems(List.of(AssessmentItem.of(problemId1, 10), AssessmentItem.of(problemId2, 90))); // 문항 2개 등록
		assessmentRepository.save(assessment);

		// 평균값으로 적용하기 시작!
		difficultyService.updateToAverageDifficulty(assessment.getId());

		Assertions.assertEquals(Difficulty.MID_LOW, assessmentRepository.findById(assessment.getId()).get().getDifficulty());
	}

	private static ProblemQueryResult createProblemQueryResult(final Difficulty difficulty) {
		return new ProblemQueryResult(
			null,
			null,
			null,
			null,
			difficulty,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);
	}

}