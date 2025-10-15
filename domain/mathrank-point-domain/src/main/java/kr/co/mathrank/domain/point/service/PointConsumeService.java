package kr.co.mathrank.domain.point.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.event.EventPayload;
import kr.co.mathrank.common.outbox.TransactionalOutboxPublisher;
import kr.co.mathrank.domain.point.dto.PointConsumeCommand;
import kr.co.mathrank.domain.point.entity.UserPoint;
import kr.co.mathrank.domain.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PointConsumeService {
	private final UserPointRepository pointRepository;
	private final TransactionalOutboxPublisher outboxPublisher;

	/**
	 * 사용자의 포인트 충전기록이 없으면, 레코드가 존재하지 않습니다.
	 * @param command
	 */
	@Transactional
	public void consume(@NotNull @Valid final PointConsumeCommand command) {
		final UserPoint userPoint = getPointOrCreate(command);

		// 포인트 차감해도 0 이상일떄
		if (userPoint.canRemovePoint(command.requirePointAmount())) {
			// 차감 완료 이벤트 발행
			userPoint.removePoint(command.requirePointAmount());
			outboxPublisher.publish(
				"mathrank-point-consume-succeeded",
				PointConsumeSucceededEvent.of(command.memberId(), command.requirePointAmount(), userPoint.getPointAmount()));
			return;
		}

		// 0 미만일떄
		// 차감 불가 이벤트 발행
		outboxPublisher.publish(
			"mathrank-point-consume-failed",
			PointConsumeFailedEvent.of(command.memberId(), command.requirePointAmount(), userPoint.getPointAmount()));
	}

	@org.jetbrains.annotations.NotNull
	private UserPoint getPointOrCreate(PointConsumeCommand command) {
		return pointRepository.findByUserId(command.memberId())
			.orElseGet(() -> {
				log.info("[PointConsumeService.consume] cannot found users point - userId: {}", command.memberId());
				return pointRepository.save(UserPoint.of(command.memberId()));
			});
	}

	// mathrank-point-consume-succeeded
	record PointConsumeSucceededEvent(
		Long memberId,
		Long consumedPointAmount,
		Long remainingPointAmount
	) implements EventPayload {
		static PointConsumeSucceededEvent of(final Long memberId, final Long consumedPointAmount, final Long remainingPointAmount) {
			return new PointConsumeSucceededEvent(memberId, consumedPointAmount, remainingPointAmount);
		}
	}

	// mathrank-point-consume-failed
	record PointConsumeFailedEvent(
		Long memberId,
		Long requiredPointAmount,
		Long remainingPointAmount
	) implements EventPayload {
		static PointConsumeFailedEvent of(final Long memberId, final Long requiredPointAmount, final Long remainingPointAmount) {
			return new PointConsumeFailedEvent(memberId, requiredPointAmount, remainingPointAmount);
		}
	}
}
