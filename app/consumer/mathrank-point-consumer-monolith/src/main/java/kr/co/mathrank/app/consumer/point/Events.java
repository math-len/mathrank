package kr.co.mathrank.app.consumer.point;

import java.time.LocalDateTime;

import kr.co.mathrank.common.event.EventPayload;

public class Events {
	record ContentOrderRegisteredEvent(
		Long memberId,
		Long contentId,
		Long contentPointCost,
		LocalDateTime registerAt
	) implements EventPayload {
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
