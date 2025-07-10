package kr.co.mathrank.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

interface OutboxEventRepository extends JpaRepository<Outbox, Long> {
}
