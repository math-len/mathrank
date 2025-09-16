package kr.co.mathrank.app.batch.rank;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.rank.service.RankFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
class RankInitializer {
	private final RankFetchService rankFetchService;

	@Scheduled(initialDelay = 5, timeUnit = TimeUnit.SECONDS)
	void initializeRankInfoToRedis() {
		rankFetchService.syncRedis();
		log.info("[RankInitializer.initializeRankInfoToRedis] rank initialization complete.");
	}
}
