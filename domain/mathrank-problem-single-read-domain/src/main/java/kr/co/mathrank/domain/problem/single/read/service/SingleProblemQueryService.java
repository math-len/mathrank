package kr.co.mathrank.domain.problem.single.read.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.common.page.PageUtil;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelQuery;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelResult;
import kr.co.mathrank.domain.problem.single.read.entity.OrderColumn;
import kr.co.mathrank.domain.problem.single.read.entity.OrderDirection;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemSolver;
import kr.co.mathrank.domain.problem.single.read.exception.CannotFoundProblemException;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemReadModelRepository;
import kr.co.mathrank.domain.problem.single.read.repository.SingleProblemSolverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SingleProblemQueryService {
	private final SingleProblemReadModelRepository singleProblemRepository;
	private final SingleProblemSolverRepository singleProblemSolverRepository;

	/**
	 * 필터 조건에 맞는 개별 문제 목록을 페이지네이션하여 조회합니다.
	 * <p>
	 * 각 문제에 대해 현재 사용자의 풀이 성공 여부(성공, 실패, 시도 안 함)를 함께 반환합니다.
	 *
	 * @param query        검색 필터 조건(문제 이름, 카테고리, 난이도 등)
	 * @param orderColumn  정렬할 컬럼. 지정하지 않으면 {@link OrderColumn#DATE}가 기본값으로 사용됩니다.
	 * @param direction    정렬 방향. 지정하지 않으면 {@link OrderDirection#DESC}가 기본값으로 사용됩니다.
	 * @param memberId     조회하는 사용자의 ID
	 * @param pageSize     한 페이지에 표시할 문제의 수 (1~20)
	 * @param pageNumber   조회할 페이지 번호 (1부터 시작)
	 * @return 페이지네이션된 문제 목록 및 관련 정보
	 */
	public PageResult<SingleProblemReadModelResult> queryPage(
		@NotNull @Valid final SingleProblemReadModelQuery query,
		OrderColumn orderColumn,
		OrderDirection direction,
		@NotNull final Long memberId,
		@NotNull @Range(min = 1, max = 20) final Integer pageSize,
		@NotNull @Range(max = 1000) final Integer pageNumber // 페이지 번호 (1부터 시작). 내부 offset 계산 시 사용됨: offset = (pageNumber - 1) * pageSize
	) {
		// 정렬기준 default 초기화
		orderColumn = orderColumn == null ? OrderColumn.DATE : orderColumn;
		direction = direction == null ? OrderDirection.DESC : direction;

		// 1. 조건에 맞는 문제 목록을 페이지네이션하여 조회
		final List<SingleProblemReadModel> readModels = singleProblemRepository.queryPage(query, pageSize, pageNumber, orderColumn, direction);
		// 2. 조건에 맞는 전체 문제 개수 조회 (페이지네이션 계산용)
		final Long count = singleProblemRepository.count(query);

		// 3. 조회된 문제 목록에 대한 현재 사용자의 풀이 기록을 Map 형태로 한 번에 조회 (메모리 오버 플러우 방지)
		final Map<Long, SingleProblemSolver> solverMap = getMap(readModels, memberId);

		// 4. 최종 결과 형태로 변환하여 반환
		return PageResult.of(
			readModels.stream()
				.map(model -> SingleProblemReadModelResult.from(
					model,
					getTryStatus(model.getId(), solverMap)))
				.toList(),
			pageNumber,
			pageSize,
			PageUtil.getNextPages(pageSize, pageNumber, count, readModels.size()
			));
	}

	/**
	 * 단일 문제 상세 정보를 조회합니다.
	 * <p>
	 * 주어진 문제 ID에 해당하는 문제를 조회하고, 요청한 사용자의 풀이 기록이 존재하면
	 * 풀이 성공 여부까지 함께 반환합니다.
	 *
	 * @param singleProblemId   조회할 문제의 ID
	 * @param requestMemberId   요청한 사용자의 ID
	 * @return                  문제 상세 정보 및 풀이 성공 여부 (풀이 기록이 없으면 null)
	 * @throws CannotFoundProblemException  해당 문제를 찾을 수 없는 경우 발생
	 */
	public SingleProblemReadModelResult getProblemWithSolverStatus(
		@NotNull final Long singleProblemId,
		@NotNull final Long requestMemberId
	) {
		return singleProblemRepository.findByIdWithSolvedInfo(singleProblemId, requestMemberId)
			.orElseThrow(() -> {
				log.info("[SingleProblemQueryService.getProblemWithSolverStatus] Problem not found. singleProblemId={}", singleProblemId);
				return new CannotFoundProblemException();
			});
	}

	/**
	 * 특정 문제에 대한 사용자의 풀이 상태를 반환합니다.
	 *
	 * @param singleProblemId 확인할 문제의 ID
	 * @param solverMap       사용자의 풀이 기록을 담고 있는 맵
	 * @return                성공 시 {@code true}, 실패 시 {@code false}, 푼 적이 없으면 {@code null}
	 */
	private Boolean getTryStatus(final Long singleProblemId, final Map<Long, SingleProblemSolver> solverMap) {
		if (solverMap.containsKey(singleProblemId)) {
			// 푼적이 있을때
			return solverMap.get(singleProblemId).isSuccess(); // 풀이 결과 리턴
		} else {
			// 푼적이 없을때
			return null;
		}
	}

	/**
	 * (메모리 오버 플러우 방지)
	 *
	 * 주어진 문제 목록에 대해 특정 사용자의 풀이 기록({@link SingleProblemSolver})을 조회하여
	 * 빠른 조회를 위한 {@code Map} 형태로 반환합니다.
	 * <p>
	 * {@code IN} 쿼리를 사용하여 풀이 기록을 한 번에 가져옵니다.
	 *
	 * @param models          조회할 문제 엔티티 목록
	 * @param requestMemberId 풀이 기록을 조회할 사용자의 ID
	 * @return                문제 ID를 key로, {@code SingleProblemSolver}를 value로 갖는 Map
	 */
	private Map<Long, SingleProblemSolver> getMap(final List<SingleProblemReadModel> models, final Long requestMemberId) {
		final Map<Long, SingleProblemSolver> map = new HashMap<>();

		final List<SingleProblemSolver> solvers = singleProblemSolverRepository.findByMemberIdAndSingleProblemReadModelIn(requestMemberId, models);

		for (final var solver : solvers) {
			map.put(solver.getSingleProblemReadModel().getId(), solver);
		}

		return map;
	}
}
