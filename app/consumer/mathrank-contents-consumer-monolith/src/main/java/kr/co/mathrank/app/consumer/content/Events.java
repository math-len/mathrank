package kr.co.mathrank.app.consumer.content;

import kr.co.mathrank.common.event.EventPayload;

public class Events {
	// mathrank-point-consume-failed
	record PointConsumeFailedEvent(
		Long orderId,
		Long memberId,
		Long requiredPointAmount,
		Long remainingPointAmount
	) implements EventPayload {
	}

	// mathrank-point-consume-succeeded
	record PointConsumeSucceededEvent(
		Long orderId,
		Long memberId,
		Long consumedPointAmount,
		Long remainingPointAmount
	) implements EventPayload {

	}
}
