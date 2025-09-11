package kr.co.mathrank.client.exception;

public class ServerResponseException extends ClientException {
    public ServerResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
