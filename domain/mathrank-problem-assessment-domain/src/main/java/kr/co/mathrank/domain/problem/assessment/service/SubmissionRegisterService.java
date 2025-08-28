package kr.co.mathrank.domain.problem.assessment.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.entity.Assessment;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentSubmission;
import kr.co.mathrank.domain.problem.assessment.exception.NoSuchAssessmentException;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentRepository;
import kr.co.mathrank.domain.problem.assessment.repository.AssessmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SubmissionRegisterService {
	private final AssessmentRepository assessmentRepository;
	private final AssessmentSubmissionRepository assessmentSubmissionRepository;

	private final TransactionalOutboxPublisher transactionalOutboxPublisher;

	/**
	 * 사용자의 시험 제출을 처리하는 메소드입니다.
	 *
	 * <p>구현 세부사항:
	 * <ul>
	 *   <li>지정된 {@code assessmentId}에 대해 {@code Assessment} 엔티티를
	 *       비관적 잠금(PESSIMISTIC_WRITE, X-Lock)으로 조회합니다.
	 *       <br>- 이를 통해 여러 사용자가 동시에 제출할 때
	 *       {@code distinctTriedMemberCount} 증가 연산의 경쟁 조건을 방지합니다.</li>
	 *
	 *   <li>해당 사용자가 처음 제출하는 경우 {@code distinctTriedMemberCount}를 증가시킵니다.
	 *       <br>- {@code Submission}은 수십만 건 이상 누적될 수 있으므로,
	 *       전체 컬렉션을 메모리에 로드하지 않고,
	 *       특정 사용자 제출 여부만 단일 쿼리(S-Lock, FOR SHARE)로 확인합니다.</li>
	 *
	 *   <li>{@code AssessmentSubmission}을 생성하여 {@code Assessment} 엔티티에 등록합니다.</li>
	 *
	 *   <li>제출이 성공적으로 등록되면,
	 *       Outbox 패턴을 통해 {@code assessment-submission-registered} 이벤트를 발행합니다.
	 *       <br>- 이벤트에는 제출 ID, 제출 시간, distinct 제출자 수 등 메타데이터가 포함됩니다.</li>
	 * </ul>
	 *
	 * @param command 제출 요청을 담은 DTO
	 * @return 새로 생성된 {@code AssessmentSubmission}의 ID
	 * @throws NoSuchAssessmentException 지정된 {@code assessmentId}에 해당하는 시험이 없을 경우 발생
	 */
	@Transactional
	public Long submit(@NotNull @Valid final SubmissionRegisterCommand command) {
		// X-Lock
		final Assessment assessment = assessmentRepository.findWithItemsForUpdate(command.assessmentId())
			.orElseThrow(() -> {
				log.info("[SubmissionRegisterService.submit] cannot found assessment - assessmentId: {}",
					command.assessmentId());
				return new NoSuchAssessmentException();
			});

		// 처음으로 제출한 사용자일때
		final boolean isFirstSubmission = isFirstTry(command.assessmentId(), command.memberId());
		if (isFirstSubmission) {
			assessment.increaseDistinctMemberCount();
		}

		// 새로운 답안지 등록
		final AssessmentSubmission assessmentSubmission = assessment.registerSubmission(
			command.memberId(),
			command.submittedAnswers(),
			command.elapsedTime(),
			isFirstSubmission
		);
		assessmentSubmissionRepository.save(assessmentSubmission);

		transactionalOutboxPublisher.publish("assessment-submission-registered", new SubmissionRegisteredEvent(
			assessment.getId(),
			command.memberId(),
			assessmentSubmission.getId(),
			assessmentSubmission.getSubmittedAt(),
			assessment.getDistinctTriedMemberCount(),
			assessmentSubmission.getElapsedTime().getSeconds(),
			isFirstSubmission
		));

		return assessmentSubmission.getId();
	}

	/**
	 * 특정 사용자가 해당 assessment에 대해 최초로 제출하는지 여부 확인
	 * - Submission은 수십만 건 이상으로 증가할 수 있음
	 * - 따라서 연관관계를 전부 메모리 로딩하지 않고,
	 *   특정 사용자 기준으로 제한된 수(최대 약 100건)만 단일 쿼리로 조회
	 * - 조회 시 S-Lock(PESSIMISTIC_READ, FOR SHARE)을 걸어 최신 커밋 기준으로 판별
	 */
	private boolean isFirstTry(final Long assessmentId, final Long memberId) {
		return assessmentSubmissionRepository.findAllByAssessmentIdAndMemberIdForShare(assessmentId, memberId).isEmpty();
	}

	record SubmissionRegisteredEvent(
		Long assessmentId,
		Long memberId,
		Long submissionId,
		LocalDateTime submittedTime,
		Long distinctTriedMemberCount,
		Long elapsedTimeSeconds,
		Boolean isFirstTry
	) implements EventPayload {
	}
}
