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

    /**
     *  这是一个一次性的密码生成工具。
     *  应用启动后，它会在控制台打印出 '123456' 加密后的字符串。
     *  使用完毕后，您可以删除或注释掉这个 @Bean。
     */
    @Bean
    public CommandLineRunner passwordEncoderRunner(PasswordEncoder passwordEncoder) {
        return args -> {
            String passwordToEncode = "123456";
            String encodedPassword = passwordEncoder.encode(passwordToEncode);

            System.out.println("\n\n==========================================================");
            System.out.println("            PASSWORD ENCODER UTILITY");
            System.out.println("----------------------------------------------------------");
            System.out.println("Plain Password: " + passwordToEncode);
            System.out.println("BCrypt Encoded: " + encodedPassword);
            System.out.println("==========================================================\n\n");
        };
    }

}
