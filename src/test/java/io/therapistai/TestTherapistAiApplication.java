package io.therapistai;

import org.springframework.boot.SpringApplication;

public class TestTherapistAiApplication {

	public static void main(String[] args) {
		SpringApplication.from(TherapistAiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
