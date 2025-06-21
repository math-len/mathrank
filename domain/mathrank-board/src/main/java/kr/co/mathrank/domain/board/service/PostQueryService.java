package kr.co.mathrank.domain.board.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.DescendingDateQueryCommand;
import kr.co.mathrank.domain.board.dto.PostQueryResults;
import kr.co.mathrank.domain.board.dto.QueryPostByOwnerIdCommand;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostQueryService {
	private final PostRepository postRepository;

	public PostQueryResults queryByOwnerIdAndDateDescending(@NotNull @Valid final QueryPostByOwnerIdCommand command) {
		final PageRequest pageRequest = PageRequest.of(0, command.pageSize(),
			Sort.by(Sort.Order.asc("ownerId"), Sort.Order.asc("boardCategory"), Sort.Order.desc("createdAt")));
		return mapToResults(
			postRepository.findAllByBoardCategoryAndOwnerId(command.boardCategory(), command.ownerId(), pageRequest));
	}

	public PostQueryResults queryPostsByDateDescending(@NotNull @Valid final DescendingDateQueryCommand command) {
		final PageRequest pageRequest = PageRequest.of(0, command.pageSize(),
			Sort.by(Sort.Order.asc("boardCategory"), Sort.Order.desc("createdAt")));
		return mapToResults(
			postRepository.findAllByBoardCategoryAndCreatedAtIsBefore(command.category(), command.current(),
				pageRequest));
	}

	private PostQueryResults mapToResults(final List<Post> posts) {
		return new PostQueryResults(
			posts.stream()
				.map(PostMapper::map)
				.toList());
	}
}
