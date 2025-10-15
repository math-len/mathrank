package kr.co.mathrank.app.consumer.point;

import java.time.LocalDateTime;

import kr.co.mathrank.common.event.EventPayload;

public class Events {
	record ContentOrderRegisteredEvent(
		Long orderId,
		Long memberId,
		Long contentId,
		Long contentPointCost,
		LocalDateTime registerAt
	) implements EventPayload {
	}
}
