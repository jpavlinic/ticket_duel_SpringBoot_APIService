package com.rit.gamifiedticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
@EnableAspectJAutoProxy
public class BackendApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // Loads .env file
        System.setProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        SpringApplication.run(BackendApplication.class, args);
	}

}
