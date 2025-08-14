package kr.co.mathrank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SingleProblemReadMonolithEventListenerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SingleProblemReadMonolithEventListenerApplication.class, args);
	}
}
