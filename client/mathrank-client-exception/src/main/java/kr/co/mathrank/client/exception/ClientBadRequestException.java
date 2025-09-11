package kr.co.mathrank.client.exception;

public class ClientBadRequestException extends ClientException {
    public ClientBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
