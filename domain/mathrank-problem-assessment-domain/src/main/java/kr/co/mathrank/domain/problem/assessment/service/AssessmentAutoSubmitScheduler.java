package kr.co.mathrank.domain.problem.assessment.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 앱을 다시 열지 않은 미제출 응시도 3시간이 지나면 제출되도록 주기적으로 처리한다. */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mathrank.assessment.auto-submit.enabled", havingValue = "true", matchIfMissing = true)
class AssessmentAutoSubmitScheduler {
	private static final int BATCH_SIZE = 100;

	private final AssessmentAutoSubmitManager assessmentAutoSubmitManager;

	@Scheduled(fixedDelay = 300_000L, initialDelay = 60_000L)
	public void autoSubmitDueAttempts() {
		for (final Long attemptId : assessmentAutoSubmitManager.findDueAttemptIds(BATCH_SIZE)) {
			try {
				assessmentAutoSubmitManager.autoSubmitIfDue(attemptId);
			} catch (RuntimeException exception) {
				log.warn("[AssessmentAutoSubmitScheduler] auto submit failed - attemptId: {}", attemptId, exception);
			}
		}
	}
}
