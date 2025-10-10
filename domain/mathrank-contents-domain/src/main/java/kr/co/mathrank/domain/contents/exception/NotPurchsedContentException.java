package kr.co.mathrank.domain.contents.exception;

public class NotPurchsedContentException extends ContentException {
	public NotPurchsedContentException() {
		super(11001, "결제되지 않은 자료입니다.");
	}
}
