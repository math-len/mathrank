package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.FreePostCreateCommand;
import kr.co.mathrank.domain.board.entity.FreePost;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class FreePostService {
	private final PostRepository postRepository;

	public String save(@NotNull @Valid final FreePostCreateCommand command) {
		final Post post = new FreePost(command.title(), command.content(), command.memberId(), command.images());
		return postRepository.save(post).getId();
	}
}
