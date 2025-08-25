package kr.co.mathrank.domain.problem.assessment.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import kr.co.mathrank.domain.problem.assessment.exception.AssessmentSubmissionRegisterException;
import kr.co.mathrank.domain.problem.assessment.exception.SubmissionTimeExceedException;
import kr.co.mathrank.domain.problem.core.Difficulty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assessment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	private Long registerMemberId;

	private String assessmentName;

	@Convert(converter = AssessmentDurationConverter.class)
	private Duration assessmentDuration;

	@Convert(converter = DifficultyConverter.class)
	private Difficulty difficulty;

	@OneToMany(mappedBy = "assessment", orphanRemoval = true, cascade = CascadeType.PERSIST)
	@OrderBy("sequence")
	private final List<AssessmentItem> assessmentItems = new ArrayList<>();

	@OneToMany(mappedBy = "assessment", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private final List<AssessmentSubmission> assessmentSubmissions = new ArrayList<>();

	@Setter
	private Long distinctTriedMemberCount = 0L;

	@CreationTimestamp
	@Setter(AccessLevel.NONE)
	private LocalDateTime createdAt;

	public static Assessment of(final Long registerMemberId, final String assessmentName, final Duration assessmentDuration) {
		final Assessment assessment = new Assessment();
		assessment.registerMemberId = registerMemberId;
		assessment.assessmentName = assessmentName;
		assessment.assessmentDuration = assessmentDuration;

		return assessment;
	}

	public void increaseDistinctMemberCount() {
		distinctTriedMemberCount++;
	}

	/**
	 * 현재 시험에 대해 사용자의 응시 기록을 생성합니다.
	 *
	 * <p>시험에 속한 문항들을 시퀀스 오름차순으로 정렬한 뒤,
	 * 사용자가 제출한 답안을 매칭하여 {@link AssessmentSubmission}에 추가합니다.</p>
	 *
	 * @param memberId 응시자 ID
	 * @param answers 각 문항에 대한 답안 목록. {@code assessmentItems}의 순서와 일치해야 합니다.
	 * @param elapsedTime 걸린 시간. {@code assessmentDuration} 보다 작거나 같아야한다.
	 *
	 * @return {@link AssessmentSubmission} 엔티티
	 */
	public AssessmentSubmission registerSubmission(final Long memberId, final List<List<String>> answers,
		final Duration elapsedTime) {
		if (this.assessmentItems.size() != answers.size()) {
			log.info(
				"[Assessment.registerSubmission] item count and answers count is not match - assessmentItem count: {}, answers Count: {}",
				assessmentItems.size(), answers.size());
			throw new AssessmentSubmissionRegisterException();
		}

		if (elapsedTime.compareTo(assessmentDuration) > 0) { // elapsedTime이 시험시간보다 큰 경우
			log.info(
				"[Assessment.registerSubmission] elapsed time overed assessment time limit - elapsedTime: {}, assessmentTime: {}",
				elapsedTime, this.assessmentDuration);
			throw new SubmissionTimeExceedException();
		}

		final AssessmentSubmission assessmentSubmission = AssessmentSubmission.of(this, memberId, elapsedTime);

		for (int i = 0; i < assessmentItems.size(); i ++) {
			assessmentSubmission.addItemSubmission(assessmentItems.get(i), answers.get(i));
		}

		assessmentSubmissions.add(assessmentSubmission);

		return assessmentSubmission;
	}

	/**
	 * 주어진 문항 목록으로 현재 평가의 문항들을 <b>완전히 교체</b>합니다.
	 * <p>
	 * 기존에 등록된 문항은 모두 제거되며(영속성 설정에 따라 orphanRemoval 시 DB에서 삭제될 수 있음),
	 * 전달된 목록의 <i>순서</i>를 기준으로 각 항목의 {@code sequence}를 1부터 재부여하고,
	 * 부모 연관({@code assessment})을 현재 엔티티로 설정합니다.
	 * </p>
	 */
	public void replaceItems(final List<AssessmentItem> items) {
		this.assessmentItems.clear();

		for (int i = 0; i < items.size(); i++) {
			final AssessmentItem item = items.get(i);
			final int sequence = i + 1; // 문제 번호 부여
			item.setSequence(sequence);
			item.setAssessment(this);

			this.assessmentItems.add(item);
		}
	}
}
