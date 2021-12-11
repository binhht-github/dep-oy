package com.spring;

import com.spring.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableConfigurationProperties(FileStorageProperties.class)
@EnableScheduling
public class GraduationProjectCompleteApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduationProjectCompleteApplication.class, args);
    }

}
