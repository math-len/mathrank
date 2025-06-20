package kr.co.mathrank.domain.board.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.ProblemQuestionPostCreateCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.entity.ProblemQuestionPost;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class ProblemQuestionPostService {
	private final PostRepository postRepository;

	public String save(@NotNull @Valid final ProblemQuestionPostCreateCommand command) {
		final Post post = new ProblemQuestionPost(command.title(), command.content(), command.memberId(),
			command.images(), command.problemId());
		return postRepository.save(post).getId();
	}
}
