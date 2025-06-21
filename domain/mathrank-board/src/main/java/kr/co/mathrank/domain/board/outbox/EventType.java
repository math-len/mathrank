package kr.co.mathrank.domain.board.outbox;

import lombok.Getter;

@Getter
public enum EventType {
	FREE_POST_CREATED_EVENT(Topic.FREE_POST_CREATED_EVENT),
	PURCHASE_POST_CREATED_EVENT(Topic.PURCHASE_POST_CREATED_EVENT),
	PROBLEM_POST_CREATED_EVENT(Topic.PROBLEM_POST_CREATED_EVENT),
	POST_DELETED_EVENT(Topic.POST_DELETED_EVENT),
	POST_UPDATED_EVENT(Topic.POST_UPDATED_EVENT);

	private String topic;

	EventType(String topic) {
		this.topic = topic;
	}

	public static class Topic {
		private static final String FREE_POST_CREATED_EVENT = "post-free-created-event";
		private static final String PURCHASE_POST_CREATED_EVENT = "post-purchase-created-event";
		private static final String PROBLEM_POST_CREATED_EVENT = "post-problem-created-event";
		private static final String POST_DELETED_EVENT = "post-deleted-event";
		private static final String POST_UPDATED_EVENT = "post-updated-event";
	}
}
