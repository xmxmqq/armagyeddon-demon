package com.blockchain.armagyeddon;

// @Scheduled이 명시된 메서드는 아규먼트를 가질 수 없고 반환타입은 void이어야 한다.

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CronTable {

    // 매일 5시 30분 0초에 실행한다.
    // "초 분 시 일 월 요일"
    @Scheduled(cron = "0 30 5 * * *")
    public void CronJob() {

        // 실행될 로직...

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("Java cron aJob expression: " + strDate);
    }

    // 고정된 지연으로 작업 예약
    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void scheduleFixedDelayTask() {

        System.out.println("Fixed delay task - " + System.currentTimeMillis() / 1000);
    }

    // 고정된 속도로 작업 예약
    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTask() {

        System.out.println("Fixed rate task - " + System.currentTimeMillis() / 1000);
    }

}
