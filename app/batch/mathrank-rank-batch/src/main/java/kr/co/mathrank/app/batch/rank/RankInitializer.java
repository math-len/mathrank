package kr.co.mathrank.app.batch.rank;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import kr.co.mathrank.domain.rank.service.RankFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class RankInitializer {
	private final RankFetchService rankFetchService;

	@EventListener(ApplicationReadyEvent.class)
	void initializeRankInfoToRedis() {
		rankFetchService.syncRedis();
		log.info("[RankInitializer.initializeRankInfoToRedis] rank initialization complete.");
	}
}
