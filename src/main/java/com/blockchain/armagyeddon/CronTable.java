package com.blockchain.armagyeddon;


import com.blockchain.armagyeddon.domain.Gye;
import com.blockchain.armagyeddon.domain.Member;

// @Scheduled이 명시된 메서드는 아규먼트를 가질 수 없고 반환타입은 void이어야 한다.
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class CronTable {

    // 요일 상관없이 매일 자정에 실행한다.
    // "초 분 시 일 월 요일"
    @Scheduled(cron = "0 0 0 * * ?")
    public void CronJob() {

        // 저축계의 경
        // 모든 참여자의 balance == targetMoney/(totalMember - 1) 시에 최초 수금
        // 수금 후 바로 turn1에게 송금
        // status = wait에서 active로 바뀌도록
        // 최초 수송금 날짜를 payDay로
        // 최초 수송금 날짜로부터 (period-1)번  반복
        // 기간 끝나면 state = expired

        // 1. 전체 계 정보를 받아온다.
        List<Gye> gyeList = GyeService.getAllGye();

        // 2. 각 계에 대해 다음과 같은 처리를 한다.
        for (Gye gye : gyeList) {

            //  2.1 계의 상태를 가져온다.
            String state = gye.getState();

            //  2.2 계의 상태가 active 라면
            if (state.equals("wait")) {

                //   2.2.1 계에 있는 멤버 정보로 잔액을 조회한다.
                List<Member> members = gye.getMembers();


                 //   2.2.2 계에 있는 멤버들의 잔액이 출금할 수 있는 수량이면, 수금을 요청한다.
                //   2.2.3 수금시, 계의 상태자 wait면, active로 변경한다.
                //   2.2.3 잔액이 부족하면, 별도 처리 없음

            }
            if (state.equals("active")) {
                //   2.2.1 계의 payDay를 가져온다.
                LocalDateTime payday = gye.getPayDay();
                //   2.2.2 계의 payday "일"을 가져온다
                //   2.2.3 현재 시간의 "일"을 가져온간
                //   2.2.4 현재시간의 "일"이 payday 의"일" 보다 크면 수금가능.

                //   2.2.5 수금이 가능하면
                //    2.2.5.1 계에 있는 멤버 정보를 가져온다.
                //    2.2.5.2 계의 있는 멤버들의 잔액이 출금할 수 있는 수량이면, 수금을 요청한다.
                //    2.2.5.3 수금시, 계의 상태자 wait면, active로 변경한다.
                //    2.2.5.3 잔액이 부족하면, 별도 처리 없음
            }

            //  2.3 계의 상태가 expired 라면 별도 처리 없음
            else if (state.equals("expired")) {

            }


        }


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
