package kr.co.mathrank.client.result;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public sealed abstract class ClientResponse<T>
	permits ClientResponse.Success, ClientResponse.StatusCodeFailure, ClientResponse.TimeOutFailure {
	public abstract boolean isSuccess();

	public final boolean isFailure() {
		return !isSuccess();
	}

	public Optional<T> get() {
		if (this.isFailure()) {
			return Optional.empty();
		}

		return Optional.of(getSuccessedResult());
	}

	public T getOrDefault(final T defaultValue) {
		if (this.isFailure()) {
			return defaultValue;
		}

		return getSuccessedResult();
	}

	public T getOrThrow() {
		return switch (this) {
			case Success<T> response -> getSuccessedResult();
			case StatusCodeFailure<T> response ->
				throw new ClientHttpStatusException(response.statusCode, response.message);
			case TimeOutFailure<T> response -> throw new ClientTimeoutException(response.message);
		};
	}

	private T getSuccessedResult() {
		final Success<T> successResponse = (Success<T>)this;
		return successResponse.result;
	}

	@RequiredArgsConstructor
	public static final class Success<T> extends ClientResponse<T> {
		private final T result;

		@Override
		public boolean isSuccess() {
			return true;
		}
	}

	@RequiredArgsConstructor
	public static final class StatusCodeFailure<T> extends ClientResponse<T> {
		private final int statusCode;
		private final String message;

		@Override
		public boolean isSuccess() {
			return false;
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static final class TimeOutFailure<T> extends ClientResponse<T> {
		private final String message;

		@Override
		public boolean isSuccess() {
			return false;
		}
	}
}
