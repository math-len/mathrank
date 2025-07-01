package kr.co.mathrank.domain.board.comment.service;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.board.comment.dto.CommentRegisterCommand;
import kr.co.mathrank.domain.board.comment.entity.Comment;
import kr.co.mathrank.domain.board.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final Snowflake snowflake;

	public Long save(@NotNull @Valid final CommentRegisterCommand command) {
		final Long id = snowflake.nextId();
		final Comment comment = Comment.of(id, command.postId(), command.content(), command.userId(), command.images());
		commentRepository.save(comment);
		return comment.getId();
	}
}
