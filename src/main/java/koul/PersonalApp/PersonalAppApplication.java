package koul.PersonalApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PersonalAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalAppApplication.class, args);
	}

}
