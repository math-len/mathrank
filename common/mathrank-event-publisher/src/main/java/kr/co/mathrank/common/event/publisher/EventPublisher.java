package kr.co.mathrank.common.event.publisher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 메시지 브로커를 통한 이벤트 발행을 추상화한 인터페이스입니다.
 * <p>
 * 목적:
 * - Kafka, RabbitMQ, ActiveMQ 등 다양한 브로커 구현체를 통합된 방식으로 사용하기 위함
 * - 상위 계층이 특정 브로커 기술에 종속되지 않도록 함
 * <p>
 * 특징:
 * - 발행 실패 시 {@link EventPublishException}을 발생시켜 호출자가 처리할 수 있도록 함
 * - 동기/비동기 전송 방식, 타임아웃, 재시도 등은 구현체에서 정의
 * <p>
 * 구현 시 권장 사항:
 * 1. 브로커별 연결/전송 예외를 적절히 변환하여 {@link EventPublishException}으로 래핑
 * 2. 전송 성공/실패 여부 및 브로커 메타데이터를 로깅 또는 모니터링 시스템에 기록
 * 3. Outbox 패턴과 결합 시, 재시도 및 중복 전송 방지 전략 고려
 */
public interface EventPublisher {

	/**
	 * 지정한 토픽으로 이벤트를 발행합니다.
	 *
	 * @param topic   브로커에서 구독하는 토픽 이름 not null
	 * @param payload 전송할 이벤트 데이터(직렬화된 문자열) not null
	 * @throws EventPublishException 발행 실패 시 발생
	 */
	void publish(@NotNull String topic, @NotNull String payload) throws EventPublishException;
}
