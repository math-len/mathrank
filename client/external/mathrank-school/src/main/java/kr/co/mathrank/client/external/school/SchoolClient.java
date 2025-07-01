package kr.co.mathrank.client.external.school;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "school-client", url = "https://open.neis.go.kr")
public interface SchoolClient {
	@GetMapping(value = "/hub/schoolInfo", consumes = "application/json")
	SchoolResponse getSchools(
		@RequestParam(value = "KEY") String key,
		@RequestParam(value = "Type") String type,
		@RequestParam(value = "pIndex") Integer pageIndex,
		@RequestParam(value = "pSize") Integer pageSize,
		@RequestParam(value = "SCHUL_NM") String schoolName
	);
}
