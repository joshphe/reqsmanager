package com.example.reqsmanager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ReqsmanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReqsmanagerApplication.class, args);
    }

}
