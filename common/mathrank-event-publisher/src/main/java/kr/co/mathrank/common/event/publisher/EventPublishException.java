package kr.co.mathrank.common.event.publisher;

/**
 * 이벤트 발행 과정에서 발생하는 예외를 표현하는 런타임 예외 클래스입니다.
 * <p>
 * 사용 목적:
 * - {@link EventPublisher} 구현체에서 메시지 브로커로 이벤트 전송 중 발생한 오류를 상위 계층에 전달
 * - 브로커별 구체적인 예외(KafkaException, AmqpException 등)를 호출자가 몰라도 되도록 추상화
 * <p>
 * 특징:
 * - 비검사 예외(RuntimeException)로 선언하여 호출 측에서 선택적으로 처리 가능
 * - 메시지 발행 실패 원인(네트워크 오류, 브로커 장애, 직렬화 실패 등)을 설명하는 메시지를 포함
 * <p>
 * 사용 예:
 * <pre>
 * if (!sendSuccess) {
 *     throw new EventPublishException("Failed to publish event to topic: " + topic);
 * }
 * </pre>
 */
public class EventPublishException extends RuntimeException {
  /**
   * 주어진 오류 메시지로 예외를 생성합니다.
   *
   * @param message 예외 원인을 설명하는 메시지
   */
  public EventPublishException(String message) {
    super(message);
  }
}
