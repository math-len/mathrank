package kr.co.mathrank.client.external.school;

import lombok.Getter;

@Getter
public enum RequestType {
	JSON("json");

	private String type;

	RequestType(String type) {
		this.type = type;
	}
}
