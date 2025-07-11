package kr.co.mathrank.app.api.school;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.mathrank.client.external.school.RequestType;
import kr.co.mathrank.client.external.school.SchoolClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SchoolQueryController {
	private final SchoolClient schoolClient;

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
