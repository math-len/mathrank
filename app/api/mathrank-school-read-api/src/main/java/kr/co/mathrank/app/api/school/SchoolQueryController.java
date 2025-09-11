package kr.co.mathrank.app.api.school;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.domain.school.SchoolQueryService;
import kr.co.mathrank.domain.school.dto.SchoolResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "학교 정보 API", description = "학교 정보를 조회하는 API 입니다.")
@RestController
@RequiredArgsConstructor
public class SchoolQueryController {
	private final SchoolQueryService schoolQueryService;

	@Operation(summary = "학교의 이름을 통한 조회 API", description = "schoolName 을 입력하지 않을 시, 빈값으로 사용됩니다.")
	@GetMapping("/api/v1/schools")
	public ResponseEntity<List<SchoolResponse>> loadInfos(
		@RequestParam(required = false)
		final String schoolName,
		@RequestParam(defaultValue = "0")
		@Range(min = 0, max = 1000)
		final Integer pageIndex,
		@Range(min = 0, max = 20)
		@RequestParam(defaultValue = "10")
		final Integer pageSize
	) {
		return ResponseEntity.ok(
			schoolQueryService.searchSchools(schoolName, pageIndex, pageSize).schools()
		);
	}

	@Operation(summary = "학교 위치 정보기반 학교 조회 API", description = "cityName에 주의해야합니다. 반드시 서울특별시, 부산광역시 와 같은 풀네임으로 검색해야 합니다. ex) 부산 (X), 서울 (X)")
	@GetMapping("/api/v1/schools/by-address")
	public ResponseEntity<List<SchoolResponse>> loadSchoolsByAddress(
		@RequestParam
		@Parameter(description = "도시이름", example = "서울특별시")
		final String cityName,
		@RequestParam
		@Parameter(description = "구 이름", example = "서구")
		final String district
	) {
		// 단일 "구"로 필터링하기 위해 포맷함
		// ex)
		// 이전: "서구" 조회 시, "강서구"도 조회 결과에 포함됨
		// 현재: "서구" 조회 시, "서구"만 조회됨
		return ResponseEntity.ok(schoolQueryService.searchSchoolsBySchoolByAddress(cityName, district).schools());
	}
}
