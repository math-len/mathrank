package kr.co.mathrank.domain.rank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.mathrank.domain.rank.entity.SolveLog;

public interface SolveLogRepository extends JpaRepository<SolveLog, Long> {
}
