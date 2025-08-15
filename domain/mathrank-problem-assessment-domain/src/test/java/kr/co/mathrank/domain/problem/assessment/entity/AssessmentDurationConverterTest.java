package kr.co.mathrank.domain.problem.assessment.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Time;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AssessmentDurationConverterTest {
	@Test
	void 디비에_저장된_후_조회됐을떄_같은값을_유지한다(){
		final AssessmentDurationConverter converter = new AssessmentDurationConverter();

		final Duration tenMinute = Duration.ofMinutes(10L);
		final Time time = converter.convertToDatabaseColumn(tenMinute);

		// 디비에 저장된 후 조회됐을떄, 같은값을 유지한다.
		Assertions.assertEquals(tenMinute, converter.convertToEntityAttribute(time));
	}
}