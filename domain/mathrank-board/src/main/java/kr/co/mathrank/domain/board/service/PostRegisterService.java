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
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostRegisterService {
	private final PostRepository postRepository;

	public String save(@NotNull @Valid final FreePostCreateCommand command) {
		final Post post = new FreePost(command.title(), command.content(), command.memberId(), LocalDateTime.now(),
			command.images());
		return savePost(post);
	}

	public String save(@NotNull @Valid final ProblemQuestionPostCreateCommand command) {
		final Post post = new ProblemQuestionPost(command.title(), command.content(), command.memberId(),
			LocalDateTime.now(), command.images(), command.problemId());
		return savePost(post);
	}

	public String save(@NotNull @Valid final PurchasePostCreateCommand command) {
		final Post post = new PurchasePost(command.title(), command.content(), command.memberId(),
			LocalDateTime.now(), command.images(), command.purchaseId());
		return savePost(post);
	}

	private String savePost(final Post post) {
		return postRepository.save(post).getId();
	}
}
