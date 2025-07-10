package kr.co.mathrank.client.external.school;

import java.util.List;

public record SchoolInfoWrapper(
	List<SchoolInfoHead> head, List<SchoolInfo> row) {
}
