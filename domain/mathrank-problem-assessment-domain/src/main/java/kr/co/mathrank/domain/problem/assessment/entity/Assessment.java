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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	@OneToMany(mappedBy = "assessment", orphanRemoval = true, cascade = CascadeType.PERSIST)
	private final List<AssessmentItem> assessmentItems = new ArrayList<>();

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
