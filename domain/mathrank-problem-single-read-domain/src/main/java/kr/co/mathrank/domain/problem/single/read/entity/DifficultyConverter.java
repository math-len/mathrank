package kr.co.mathrank.domain.problem.single.read.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.co.mathrank.domain.problem.core.Difficulty;

@Converter
public class DifficultyConverter implements AttributeConverter<Difficulty, Integer> {
	private static final Map<Integer, Difficulty> DIFFICULTY_LOOK_UP = Arrays.stream(Difficulty.values())
		.collect(Collectors.toMap(Difficulty::getPriority, Function.identity()));

	@Override
	public Integer convertToDatabaseColumn(Difficulty attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getPriority();
	}

	@Override
	public Difficulty convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		final int primitive = dbData;
		return DIFFICULTY_LOOK_UP.getOrDefault(primitive, null);
	}
}
