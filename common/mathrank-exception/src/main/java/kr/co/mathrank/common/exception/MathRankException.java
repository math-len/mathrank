package kr.co.mathrank.common.exception;

import lombok.Getter;

/**
 * See <a href="https://snow-quasar-645.notion.site/ErrorCode-Documentation-231631417ede8039ac1fc2c0fa272450?pvs=74">
 * ErrorCode Documentation (Notion)
 * </a>
 */
@Getter
public class MathRankException extends RuntimeException {
	private final int code;

	public MathRankException(final int code, final String message) {
		super(message);
		this.code = code;
	}
}
