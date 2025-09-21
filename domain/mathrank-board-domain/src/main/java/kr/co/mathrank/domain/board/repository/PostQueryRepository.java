package kr.co.mathrank.domain.board.repository;

import java.util.List;

import kr.co.mathrank.domain.board.dto.PostPageQuery;
import kr.co.mathrank.domain.board.entity.Post;

public interface PostQueryRepository {
	List<Post> findAllByPage(
		PostPageQuery postQuery,
		int pageSize,
		int pageNumber
	);

	Long count(PostPageQuery postQuery);
}
