package kr.co.mathrank.client.external.school;

import java.util.List;

public record SchoolResponse(
	List<SchoolInfoWrapper> schoolInfo
) {
}