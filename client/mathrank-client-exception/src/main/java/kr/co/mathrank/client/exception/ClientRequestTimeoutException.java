package kr.co.mathrank.client.exception;

public class ClientRequestTimeoutException extends ClientException {
    public ClientRequestTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
