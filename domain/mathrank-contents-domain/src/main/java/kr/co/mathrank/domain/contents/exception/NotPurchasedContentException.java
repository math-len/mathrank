package kr.co.mathrank.domain.contents.exception;

public class NotPurchasedContentException extends ContentException {
	public NotPurchasedContentException() {
		super(11001, "결제되지 않은 자료입니다.");
	}
}
