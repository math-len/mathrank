package kr.co.mathrank.domain.board.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.board.dto.DescendingDateQueryCommand;
import kr.co.mathrank.domain.board.dto.PostQueryResults;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class PostDescendingDateQueryService {
	private final PostRepository postRepository;

	public PostQueryResults queryPostsByDateDescending(@NotNull @Valid final DescendingDateQueryCommand command) {
		final PageRequest pageRequest = PageRequest.of(0, command.pageSize(),
			Sort.by(Sort.Order.asc("boardCategory"), Sort.Order.desc("createdAt")));
		return new PostQueryResults(
			postRepository.findAllByBoardCategoryAndCreatedAtIsBefore(command.category(), command.current(), pageRequest)
				.stream()
				.map(PostMapper::map)
				.toList());
	}
}
