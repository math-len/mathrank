package kr.co.mathrank.domain.board.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.comment.dto.CommentResult;
import kr.co.mathrank.domain.board.comment.dto.CommentResults;
import kr.co.mathrank.domain.board.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class CommentQueryService {
	private final CommentRepository commentRepository;

	public CommentResults queryComments(@NotNull final Long postId) {
		return new CommentResults(commentRepository.queryAllByPostIdSortedByCreatedAt(postId)
			.stream()
			.map(CommentResult::from)
			.toList());
	}
}
