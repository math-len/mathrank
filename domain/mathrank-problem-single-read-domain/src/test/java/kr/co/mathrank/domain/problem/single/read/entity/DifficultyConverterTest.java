package kr.co.mathrank.domain.problem.single.read.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import kr.co.mathrank.domain.problem.core.Difficulty;

class DifficultyConverterTest {
	private DifficultyConverter converter = new DifficultyConverter();

	@Test
	void 숫자에서_매핑_테스트() {
		Assertions.assertEquals(Difficulty.LOW, converter.convertToEntityAttribute(10));
	}

	@Test
	void 없을땐_null_리턴() {
		// 없는 값
		Assertions.assertNull(converter.convertToEntityAttribute(12));
	}
}
