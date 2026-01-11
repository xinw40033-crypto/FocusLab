package com.focuslab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.focuslab.common.repository")
public class FocusTrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusTrainingApplication.class, args);
    }

}