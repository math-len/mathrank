package kr.co.mathrank.domain.point.entity;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class BigDecimalConverter implements AttributeConverter<BigDecimal, String> {
	@Override
	public String convertToDatabaseColumn(BigDecimal attribute) {
		return attribute == null ? "0" : attribute.toString();
	}

	@Override
	public BigDecimal convertToEntityAttribute(String dbData) {
		return new BigDecimal(dbData);
	}
}
