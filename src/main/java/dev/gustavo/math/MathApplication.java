package dev.gustavo.math;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MathApplication {

	public static void main(String[] args) {
		SpringApplication.run(MathApplication.class, args);
	}

}
