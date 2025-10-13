package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQuery;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.ProblemSolutionResult;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentItem;
import kr.co.mathrank.domain.problem.assessment.exception.CannotGetSolutionException;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchSubmissionException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AssessmentSolutionQueryService {
	private final TransactionTemplate transactionTemplate;
	private final AssessmentRepository assessmentRepository;
	private final ProblemQueryManager problemQueryManager;

	/**
	 * 사용자가 문제를 푼 경우에만 정답 조회가 가능합니다.
	 * @param query
	 */
	public AssessmentSolutionQueryResult querySolutions(@NotNull @Valid final AssessmentSolutionQuery query) {
		// 트랜잭션 내에서 조회
		final List<Long> problemIds = transactionTemplate.execute(status -> {
			final Assessment solvedAssessment = getSubmittedAssessment(query.assessmentId(), query.requestMemberId(), query.requestMemberRole());
			return solvedAssessment.getAssessmentItems().stream()
				.map(AssessmentItem::getProblemId)
				.toList();
		});

		return new AssessmentSolutionQueryResult(problemIds.stream()
			.map(problemQueryManager::getProblemInfo)
			.map(ProblemSolutionResult::from)
			.toList());
	}

	private Assessment getSubmittedAssessment(final Long assessmentId, final Long requestMemberId, final Role role) {
		return assessmentRepository.findWithSubmissions(assessmentId).stream()
			.peek(assessment -> validatePermission(assessment, requestMemberId, role))
			.findAny()
			.orElseThrow(() -> {
				log.info(
					"[AssessmentSolutionQueryService.getSubmittedAssessment] assessment not solved - assessmentId: {}, requestMemberId: {}",
					assessmentId, requestMemberId);
				return new NoSuchSubmissionException();
			});
	}

	private void validatePermission(final Assessment assessment, final Long requestMemberId, final Role role) {
		// 관리자면 pass
		if (role == Role.ADMIN) {
			return;
		}

		// 풀이기록이 있을때 통과
		if (assessment.getAssessmentSubmissions().stream()
			.anyMatch(assessmentSubmission -> assessmentSubmission.getMemberId().equals(requestMemberId))) {
			return;
		}

		log.info(
			"[AssessmentSolutionQueryService.validatePermission] cannot access assessment solution - assessmentId: {}, requestMemberId: {}, requestMemberRole: {}",
			assessment.getId(), requestMemberId, role);
		throw new CannotGetSolutionException();
	}
}
