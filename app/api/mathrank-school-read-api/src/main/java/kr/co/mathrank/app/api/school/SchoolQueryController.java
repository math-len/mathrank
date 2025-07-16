package kr.co.mathrank.app.api.school;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "학교 정보 API", description = "학교 정보를 조회하는 API 입니다.")
@RestController
@RequiredArgsConstructor
public class SchoolQueryController {
	private final SchoolClient schoolClient;

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
			schoolClient.getSchools(RequestType.JSON.getType(), pageIndex, pageSize, schoolName)
				.getSchoolInfo().stream()
				.map(SchoolResponse::from)
				.toList());
	}
}
