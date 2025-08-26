package kr.co.mathrank.domain.problem.assessment.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.course.CourseQueryResult;
import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.assessment.AssessmentReadDomainConfiguration;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDetailReadModelResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDetailResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemDetail;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentItemReadModelDetailResult;
import kr.co.mathrank.domain.problem.assessment.dto.CourseDetailResult;
import lombok.RequiredArgsConstructor;

@Service
@CacheConfig(cacheNames = AssessmentReadDomainConfiguration.ASSESSMENT_READ_MODEL_CACHE_NAME)
@RequiredArgsConstructor
public class AssessmentDetailReadService {
	private final AssessmentQueryService assessmentQueryService;
	private final ProblemQueryManager problemQueryManager;
	private final CourseQueryManager courseQueryManager;

	@Cacheable(key = "#assessmentId")
	public AssessmentDetailReadModelResult getDetail(@NotNull final Long assessmentId) {
		final AssessmentDetailResult detailResult = assessmentQueryService.getAssessmentDetails(assessmentId);
		return AssessmentDetailReadModelResult.from(
			detailResult,
			detailResult.itemDetails().stream()
				.map(this::mapToReadModel)
				.toList());
	}

	private AssessmentItemReadModelDetailResult mapToReadModel(final AssessmentItemDetail itemDetail) {
		final ProblemQueryResult problemQueryResult = problemQueryManager.getProblemInfo(itemDetail.problemId());
		final CourseQueryResult courseQueryResult = courseQueryManager.getCourseInfo(problemQueryResult.path());

		return new AssessmentItemReadModelDetailResult(
			itemDetail.problemId(),
			problemQueryResult.imageSource(),
			problemQueryResult.memberId(),
			problemQueryResult.path(),
			CourseDetailResult.from(courseQueryResult),
			problemQueryResult.difficulty(),
			problemQueryResult.type(),
			problemQueryResult.schoolCode(),
			problemQueryResult.createdAt(),
			problemQueryResult.year()
		);
	}
}
