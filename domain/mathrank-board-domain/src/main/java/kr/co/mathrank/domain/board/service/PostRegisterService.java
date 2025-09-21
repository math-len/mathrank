package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.PostRegisterCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PostRegisterService {
	private final PostRepository postRepository;

	public Long register(@NotNull @Valid final PostRegisterCommand command) {
		final Post post = command.toEntity();
		postRepository.save(post);
		log.info("[PostRegisterService.register] post saved - postId: {}", post.getId());
		return post.getId();
	}
}
