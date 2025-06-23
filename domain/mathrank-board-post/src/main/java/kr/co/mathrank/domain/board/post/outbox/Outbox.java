package kr.co.mathrank.domain.board.post.outbox;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Outbox {
	private EventType eventType;

	private LocalDateTime createdAt;
}
