package kr.co.mathrank.domain.board.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.DescendingDateQueryCommand;
import kr.co.mathrank.domain.board.dto.PostQueryResults;
import kr.co.mathrank.domain.board.entity.BoardCategory;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostDescendingDateQueryService {
	private final PostRepository postRepository;

	public PostQueryResults queryProblemPosts(@NotNull @Valid final DescendingDateQueryCommand command) {
		return this.queryPosts(BoardCategory.PROBLEM_QUESTION, command.current(), command.pageSize());
	}

	public PostQueryResults queryFreePosts(@NotNull @Valid final DescendingDateQueryCommand command) {
		return this.queryPosts(BoardCategory.FREE_BOARD, command.current(), command.pageSize());
	}

	public PostQueryResults queryPurchasePosts(@NotNull @Valid final DescendingDateQueryCommand command) {
		return this.queryPosts(BoardCategory.PURCHASE_QUESTION, command.current(), command.pageSize());
	}

	private PostQueryResults queryPosts(final BoardCategory category, final LocalDateTime current, final int pageSize) {
		final PageRequest pageRequest = PageRequest.of(0, pageSize,
			Sort.by(Sort.Order.asc("boardCategory"), Sort.Order.desc("createdAt")));
		return new PostQueryResults(
			postRepository.findAllByBoardCategoryAndCreatedAtIsBefore(category, current, pageRequest)
				.stream()
				.map(PostMapper::map)
				.toList());
	}
}
