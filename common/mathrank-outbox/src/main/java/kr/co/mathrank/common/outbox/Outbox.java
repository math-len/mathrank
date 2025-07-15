package kr.co.mathrank.common.outbox;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
class Outbox {
    @Id
    @Column(name = "event_id")
    private Long id;

    @Column(name = "event_payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "event_topic")
    private String topic;

    @CreationTimestamp
    @Column(name = "event_saved_at")
    private LocalDateTime savedAt;

    static Outbox of(final Long id, final String topic, final String payload) {
        final Outbox outbox = new Outbox();
        outbox.id = id;
        outbox.topic = topic;
        outbox.payload = payload;

        return outbox;
    }
}
