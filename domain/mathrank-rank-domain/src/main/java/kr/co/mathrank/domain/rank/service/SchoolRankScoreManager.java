package kr.co.mathrank.domain.rank.service;

import kr.co.mathrank.domain.rank.entity.SchoolScore;
import kr.co.mathrank.domain.rank.exception.SchoolScoreConflictException;
import kr.co.mathrank.domain.rank.repository.SchoolScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class SchoolRankScoreManager {
    private final SchoolScoreRepository schoolScoreRepository;

    @Transactional
    @Retryable(
            retryFor = SchoolScoreConflictException.class,
            maxAttempts = 10,
            backoff = @Backoff(
                    delay = 1000 // 첫 번째 재시도 대기 시간 (1000ms = 1초)
            ))
    public void addSchoolScore(String schoolCode, Integer score) {
        final SchoolScore schoolScore = schoolScoreRepository.findBySchoolCode(schoolCode)
                .orElseGet(() -> SchoolScore.create(schoolCode));
        schoolScore.addScore(score);
        schoolScoreRepository.save(schoolScore);
    }
}
