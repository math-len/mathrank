package kr.co.mathrank.domain.problem.assessment;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssessmentTimeConfiguration {
	@Bean
	public Clock assessmentClock() {
		return Clock.systemUTC();
	}
}
