package kr.co.mathrank.domain.problem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Difficulty {
	KILLER(60),
	HIGH(50),
	MID_HIGH(40),
	MID(30),
	MID_LOW(20),
	LOW(10);

	private final int priority;
}
