package kr.co.mathrank.domain.rank.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.problem.core.Difficulty;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemRankScoreManager {
	private final Map<Difficulty, Integer> difficultyScore = new HashMap<>();

	{
		// 1. 티어 산정 방식: 킬러를 20점으로 상 10점 중상 8점, 중 5점, 중하 2점, 하 1점
		difficultyScore.put(Difficulty.KILLER, 20);
		difficultyScore.put(Difficulty.HIGH, 10);
		difficultyScore.put(Difficulty.MID_HIGH, 8);
		difficultyScore.put(Difficulty.MID,5);
		difficultyScore.put(Difficulty.MID_LOW, 2);
		difficultyScore.put(Difficulty.LOW, 1);
	}

	public int mapToScore(final Difficulty difficulty) {
		return difficultyScore.getOrDefault(difficulty, 0);
	}
}
