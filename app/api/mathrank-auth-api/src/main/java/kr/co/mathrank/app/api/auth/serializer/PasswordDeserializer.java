package kr.co.mathrank.app.api.auth.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import kr.co.mathrank.domain.auth.entity.Password;

public class PasswordDeserializer extends JsonDeserializer<Password> {
	@Override
	public Password deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		final String password = p.getValueAsString();
		return new Password(password);
	}
}
