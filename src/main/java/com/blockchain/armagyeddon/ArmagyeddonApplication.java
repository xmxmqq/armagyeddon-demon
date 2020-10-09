package com.blockchain.armagyeddon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 작업 스케쥴러 활성화
public class ArmagyeddonApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ArmagyeddonApplication.class, args);
    }

}
