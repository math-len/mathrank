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
	private final PostMemberManager postMemberManager;

	public Long register(@NotNull @Valid final PostRegisterCommand command) {
		final String nickName = postMemberManager.fetchMemberNickName(command.memberId());
		final Post post = command.toEntity(nickName);
		postRepository.save(post);
		log.info("[PostRegisterService.register] post saved - postId: {}, postType: {}", post.getId(), post.getPostType());
		return post.getId();
	}
}
