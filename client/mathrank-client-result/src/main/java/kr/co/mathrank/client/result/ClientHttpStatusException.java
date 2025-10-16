package kr.co.mathrank.client.result;

import lombok.Getter;

@Getter
public class ClientHttpStatusException extends ClientException {
	private static final String MESSAGE_FORMAT = "client error occurred - statusCode: %s, message: %s";

	private final int statusCode;
	private final String reasonPhrase;

	public ClientHttpStatusException(final int statusCode, final String message) {
		super(createMessage(statusCode, message));
		this.statusCode = statusCode;
		this.reasonPhrase = message;
	}

	private static String createMessage(final int statusCode, final String message) {
		return String.format(MESSAGE_FORMAT, statusCode, message);
	}
}
