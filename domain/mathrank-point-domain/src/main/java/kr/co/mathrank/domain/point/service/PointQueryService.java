package kr.co.mathrank.domain.point.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.point.dto.PointQuery;
import kr.co.mathrank.domain.point.dto.PointQueryResult;
import kr.co.mathrank.domain.point.entity.UserPoint;
import kr.co.mathrank.domain.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointQueryService {
	private final UserPointRepository userPointRepository;

	/**
	 * 멤버를 찾을 수 없으면 0 을 응답합니다.
	 * @param query
	 * @return
	 */
	public PointQueryResult queryPoint(
		@NotNull @Valid final PointQuery query
	) {
		final Long remainPoint = userPointRepository.findByUserId(query.targetMemberId())
			.map(UserPoint::getPointAmount)
			.orElseGet(() -> 0L);

		return new PointQueryResult(remainPoint);
	}
}
