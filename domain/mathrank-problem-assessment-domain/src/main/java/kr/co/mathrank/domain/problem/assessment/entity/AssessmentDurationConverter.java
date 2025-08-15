package kr.co.mathrank.domain.problem.assessment.entity;

import java.sql.Time;
import java.time.Duration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AssessmentDurationConverter implements AttributeConverter<Duration, Time> {
	@Override
	public Time convertToDatabaseColumn(Duration attribute) {
		return new Time(attribute.toMillis());
	}

	@Override
	public Duration convertToEntityAttribute(Time dbData) {
		return Duration.ofMillis(dbData.getTime());
	}
}
