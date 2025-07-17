package kr.co.mathrank.domain.problem.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.dto.CourseQueryContainsParentsResult;
import kr.co.mathrank.domain.problem.dto.CourseQueryResult;
import kr.co.mathrank.domain.problem.dto.CourseQueryResults;
import kr.co.mathrank.domain.problem.entity.Path;
import kr.co.mathrank.domain.problem.repository.CourseRepository;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class CourseQueryService {
	private final CourseRepository courseRepository;

	public CourseQueryResults queryChildes(@NotNull final String pathSource) {
		final Path path = new Path(pathSource);

		return new CourseQueryResults(courseRepository.queryChildes(path.getPath(), path.getChildLength())
			.stream()
			.map(CourseQueryResult::from)
			.toList());
	}

	// 삭제된 상위 경로는 출력되지 않음
	// 쿼리가 개당 한번씩 호출될것
	public CourseQueryContainsParentsResult queryParents(@NotNull final String path) {
		final Path basePath = new Path(path);
		final CourseQueryResult result = courseRepository.findByPath(basePath)
			.map(CourseQueryResult::from)
			.orElseGet(CourseQueryResult::none);
		final List<CourseQueryResult> parents = courseRepository.findAllByPathIn(basePath.getUpperPaths())
			.stream()
			.map(CourseQueryResult::from)
			.toList();

		return new CourseQueryContainsParentsResult(result, parents);
	}
}
