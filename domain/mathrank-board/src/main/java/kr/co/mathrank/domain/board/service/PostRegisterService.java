package kr.co.mathrank.domain.board.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.FreePostCreateCommand;
import kr.co.mathrank.domain.board.dto.ProblemQuestionPostCreateCommand;
import kr.co.mathrank.domain.board.dto.PurchasePostCreateCommand;
import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.ProblemQuestionPost;
import kr.co.mathrank.domain.board.entity.PurchasePost;
import kr.co.mathrank.domain.board.outbox.EventType;
import kr.co.mathrank.domain.board.outbox.Outbox;
import kr.co.mathrank.domain.board.outbox.OutboxEventPublisher;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostRegisterService {
	private final PostRepository postRepository;
	private final OutboxEventPublisher outboxEventPublisher;

	public String save(@NotNull @Valid final FreePostCreateCommand command) {
		final LocalDateTime now = getCurrentTime();
		final Post post = new FreePost(command.title(), command.content(), command.memberId(), now,
			command.images());
		post.setOutbox(new Outbox(EventType.FREE_POST_CREATED_EVENT, now));
		return savePost(post);
	}

	public String save(@NotNull @Valid final ProblemQuestionPostCreateCommand command) {
		final LocalDateTime now = getCurrentTime();
		final Post post = new ProblemQuestionPost(command.title(), command.content(), command.memberId(), now,
			command.images(), command.problemId());
		post.setOutbox(new Outbox(EventType.PROBLEM_POST_CREATED_EVENT, now));
		return savePost(post);
	}

	public String save(@NotNull @Valid final PurchasePostCreateCommand command) {
		final LocalDateTime now = getCurrentTime();
		final Post post = new PurchasePost(command.title(), command.content(), command.memberId(), now,
			command.images(), command.purchaseId());
		post.setOutbox(new Outbox(EventType.PURCHASE_POST_CREATED_EVENT, now));
		return savePost(post);
	}

	private LocalDateTime getCurrentTime() {
		return LocalDateTime.now();
	}

	private String savePost(final Post post) {
		final String id = postRepository.save(post).getId();
		outboxEventPublisher.publishMessage(post);
		return id;
	}
}
