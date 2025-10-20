package kr.co.mathrank.domain.board.service;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.board.MathrankBoardCacheConfiguration;
import kr.co.mathrank.domain.board.dto.PostDetailQueryResult;
import kr.co.mathrank.domain.board.dto.PostPageQuery;
import kr.co.mathrank.domain.board.dto.PostPageQueryResult;
import kr.co.mathrank.domain.board.entity.Post;
import kr.co.mathrank.domain.board.exception.CannotFoundPostException;
import kr.co.mathrank.domain.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PostQueryService {
	private final PostRepository postRepository;

	@Cacheable(
		cacheNames = MathrankBoardCacheConfiguration.MATHRANK_POST_PAGE_CACHE,
		condition = "#pageSize <= 3"
	)
	public PageResult<PostPageQueryResult> pageQuery(
		@NotNull final PostPageQuery query,
		@NotNull @Range(min = 1, max = 20) final Integer pageSize,
		@NotNull @Range(min = 1, max = 1000) final Integer pageNumber
	) {
		final List<PostPageQueryResult> posts = postRepository.findAllByPage(query, pageSize, pageNumber)
			.stream()
			.map(PostPageQueryResult::from)
			.toList();
		final Long count = postRepository.count(query);

		return PageResult.of(
			posts,
			pageNumber,
			pageSize,
			PageUtil.getNextPages(pageSize, pageNumber, count, posts.size())
		);
	}

	@Cacheable(
		cacheNames = MathrankBoardCacheConfiguration.MATHRANK_POST_SINGLE_CACHE
	)
	public PostDetailQueryResult getDetail(
		@NotNull final Long postId
	) {
		final Post post = postRepository.findByIdWithComments(postId)
			.orElseThrow(() -> {
				log.info("[PostQueryService.getDetail] cannot found post - postId: {}", postId);
				return new CannotFoundPostException();
			});

		return PostDetailQueryResult.from(post);
	}
}
