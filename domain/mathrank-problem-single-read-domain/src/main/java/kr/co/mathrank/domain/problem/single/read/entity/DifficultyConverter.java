package kr.co.mathrank.domain.problem.single.read.entity;

import java.util.Arrays;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.co.mathrank.domain.problem.core.Difficulty;

@Converter
public class DifficultyConverter implements AttributeConverter<Difficulty, Integer> {
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
		return Arrays.stream(Difficulty.values())
			.filter(diff -> diff.getPriority() == primitive)
			.findAny()
			.orElse(null);
	}
}
