package kr.co.mathrank.client.external.school;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class SchoolResponse {
	private final List<SchoolInfoWrapper> schoolInfo;

	public List<SchoolInfo> getSchoolInfo() {
		if (this.schoolInfo == null) {
			return Collections.emptyList();
		}
		return schoolInfo.stream()
			.map(SchoolInfoWrapper::row)
			.filter(Objects::nonNull)
			.findAny()
			.orElseGet(Collections::emptyList);
	}
}