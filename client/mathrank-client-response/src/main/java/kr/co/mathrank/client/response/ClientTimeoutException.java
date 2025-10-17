package kr.co.mathrank.client.response;

public class ClientTimeoutException extends ClientException {
	public ClientTimeoutException(String message) {
		super(message);
	}
}
