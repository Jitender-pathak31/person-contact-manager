package com.example.contacts;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ContactManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactManagerApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDatabase(PersonRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Person("Ada", "Lovelace", "ada@analytical.engine", "+44 20 7946 0101"));
                repository.save(new Person("Alan", "Turing", "alan@bletchley.org", "+44 20 7946 0202"));
                repository.save(new Person("Grace", "Hopper", "grace@navy.mil", "+1 202 555 0143"));
            }
        };
    }
}

