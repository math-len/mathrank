package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQuery;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQueryPageResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AssessmentQueryService {
	private final AssessmentRepository assessmentRepository;

	public AssessmentQueryPageResult pageQuery(
		@NotNull final AssessmentQuery assessmentQuery,
		@NotNull @Range(min = 1, max = 20) final Integer pageSize,
		@NotNull @Range(min = 1, max = 1000)final Integer pageNumber
	) {
		final List<Assessment> queryResults = assessmentRepository.query(assessmentQuery, pageSize, pageNumber);
		final Long totalCount = assessmentRepository.count(assessmentQuery);
		final List<Integer> nextPages = PageUtil.getNextPages(pageSize, pageNumber, totalCount, queryResults.size());

		return AssessmentQueryPageResult.from(
			queryResults,
			pageNumber,
			pageSize,
			nextPages
		);
	}
}
