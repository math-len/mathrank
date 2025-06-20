package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.PurchasePostCreateCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.PurchasePost;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PurchasePostService {
	private final PostRepository postRepository;

	public void save(@NotNull @Valid final PurchasePostCreateCommand command) {
		final Post post = new PurchasePost(command.title(), command.content(), command.memberId(),
			command.images(), command.purchaseId());
		postRepository.save(post);
	}
}
