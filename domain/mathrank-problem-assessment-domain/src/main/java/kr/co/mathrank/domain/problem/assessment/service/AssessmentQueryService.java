package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDetailResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentPageQuery;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentPageQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrderDirection;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrder;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AssessmentQueryService {
	private final AssessmentRepository assessmentRepository;

	public AssessmentDetailResult getAssessmentDetails(@NotNull final Long assessmentId) {
		final Assessment assessment = assessmentRepository.findWithItems(assessmentId)
			.orElseThrow(() -> {
				log.info("[AssessmentQueryService.getAssessmentDetails] cannot found assessment - assessmentId: {}",
					assessmentId);
				return new NoSuchAssessmentException();
			});
		return AssessmentDetailResult.from(assessment);

	}

	public PageResult<AssessmentPageQueryResult> pageQuery(
		@NotNull final AssessmentPageQuery assessmentQuery,
		@NotNull final AssessmentOrder assessmentOrder,
		@NotNull final AssessmentOrderDirection direction,
		@NotNull @Range(min = 1, max = 20) final Integer pageSize,
		@NotNull @Range(min = 1, max = 1000)final Integer pageNumber
	) {
		final List<Assessment> queryResults = assessmentRepository.query(assessmentQuery, pageSize, pageNumber, assessmentOrder, direction);
		final Long totalCount = assessmentRepository.count(assessmentQuery);
		final List<Integer> nextPages = PageUtil.getNextPages(pageSize, pageNumber, totalCount, queryResults.size());

		return PageResult.of(
			queryResults.stream()
				.map(AssessmentPageQueryResult::from)
				.toList(),
			pageNumber,
			pageSize,
			nextPages
		);
	}
}
