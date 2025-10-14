package kr.co.mathrank.domain.point.exception;

public class CannotFoundPointProductException extends PointException {
	public CannotFoundPointProductException() {
		super(10001, "Point 상품을 찾을 수 없습니다.");
	}
}
