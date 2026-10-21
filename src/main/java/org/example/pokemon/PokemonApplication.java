package org.example.pokemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PokemonApplication {
	// Sobe o Tomcat na porta 8080.
	public static void main(String[] args) {
		SpringApplication.run(PokemonApplication.class, args);
	}

}
