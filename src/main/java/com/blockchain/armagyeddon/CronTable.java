package com.blockchain.armagyeddon;


import com.blockchain.armagyeddon.domain.Gye;
import com.blockchain.armagyeddon.domain.Member;

// @Scheduled이 명시된 메서드는 아규먼트를 가질 수 없고 반환타입은 void이어야 한다.
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Component
public class CronTable {

    // 요일 상관없이 매일 자정에 실행한다.
    // "초 분 시 일 월 요일"
    @Scheduled(cron = "0 0 0 * * ?")
    public void CronJob() {

        GyeService gyeService;

        boolean isCollectable = true;

        // A. 로그인 (토큰 받아오기)
        GyeService.getJWT();

        // B. 전체 계 정보를 받아온다.
        List<Gye> gyeList = GyeService.getValidateGye();

        // C. 각 계에 대해 다음과 같은 처리를 한다.
        for (Gye gye : gyeList) {

            // D. 참여 인원이 갖춰졌는지 확인한다.
            int totalMember = gye.getTotalMember();
            if (totalMember != gye.getMembers().size()) {
                continue;
            }

            //   1. 계의 상태를 가져온다.
            String state = gye.getState();

            //   2. 계에 있는 멤버 정보로 잔액을 조회한다.
            List<Member> members = gye.getMembers();

            // 한 달에 1인에게 수금되는 액수
            Long targetMonthFee = Long.valueOf(Math.round(gye.getTargetMoney() / (gye.getTotalMember() - 1)));

            //  3. 계의 상태가 wait 라면
            if (state.equals("wait")) {

                //   3.2 잔액 확인
                for (Member mem : members) {

                    targetMonthFee = ( gye.getType().equals("낙찰계") ?
                            GyeService.calculateMoney(gye.getId(),mem.getEmail(),0 ):
                            targetMonthFee );
                    double balance = GyeService.getBalanceOf(mem.getEmail());

                    if (balance < targetMonthFee) {
                        isCollectable = false;
                        break;
                    }
                }

                if (!isCollectable) {
                    break;
                }

                //   3.3 계에 있는 멤버들의 잔액이 출금할 수 있는 수량이면, 수송금을 요청한다.
                for (Member mem : members) {

                    //저축계
                    if (mem.getTurn() == 1 && gye.getType().equals("저축계")) {

                        GyeService.sendToken(gye.getId(), mem.getEmail(), Integer.toString(gye.getTargetMoney()));
                    } else if (gye.getType().equals("저축계")) {
                        GyeService.collectToken(mem.getEmail(), gye.getId(), Long.toString(targetMonthFee));
                    }

                    //낙찰계
                    if (mem.getTurn() == 1 && gye.getType().equals("낙찰계")) {
                        GyeService.collectToken(mem.getEmail(), gye.getId(), Long.toString(targetMonthFee));
                        GyeService.sendToken(gye.getId(), mem.getEmail(), Integer.toString(gye.getTargetMoney()));
                    } else if (gye.getType().equals("낙찰계")) {
                        GyeService.collectToken(mem.getEmail(), gye.getId(), Long.toString(targetMonthFee));
                    }
                }
                
              //   3.4 수송금완료 후, payDay 추출

                LocalDateTime payDay = LocalDateTime.now();
                String strDate = payDay.toString();
                System.out.println("the gye starts at : " + strDate);

                //   3.5 계의 상태를 active로 변경한다. (계 활성화)
                GyeService.updateGye(gye.getId(), "active", strDate);
            }

            String sendtoken_email = null;
            long target_month_money = 0;

            //  4. 계의 상태가 active 라면
            if (state.equals("active")) {

                //   4.1 계의 payDay를 가져온다.
                //   4.2 계의 payday "일"을 가져온다.
                LocalDateTime payDay = LocalDateTime.from(gye.getPayDay());

                //   4.3 현재 시간의 "일"을 가져온다.
                LocalDateTime today = LocalDateTime.from(LocalDateTime.now());

                // turn이 2 이상인 member의 순서
                long nowTurn = Period.between(payDay.toLocalDate(), today.toLocalDate()).toTotalMonths() + 1;

                //   4.4 현재시간의 "일"이 payday 의"일" 보다 클 때
                if (!(payDay.getDayOfMonth() <= today.getDayOfMonth() && payDay.getMonthValue() < today.getMonthValue())) continue;


                //   4.4.1 잔액 조회
                for (Member mem : members) {
                    double balance = GyeService.getBalanceOf(mem.getEmail());
                    targetMonthFee = ( gye.getType().equals("낙찰계") ?
                            GyeService.calculateMoney(gye.getId(),mem.getEmail(),(int)(nowTurn-1) ):
                            targetMonthFee );

                    if (balance < targetMonthFee) {
                        isCollectable = false;
                        break;
                    }
                }

                if (!isCollectable) {
                    continue;
                }

                //   4.4.2 계에 있는 멤버들의 잔액이 출금할 수 있는 수량이면, 수송금을 요청한다.

                for (Member mem : members) {

                    targetMonthFee = (gye.getType().equals("낙찰계") ?
                            GyeService.calculateMoney(gye.getId(), mem.getEmail(), (int) (nowTurn - 1)) :
                            targetMonthFee);

                    //저축계
                    if (mem.getTurn() == nowTurn && gye.getType().equals("저축계")) {
                        GyeService.sendToken(gye.getId(), mem.getEmail(), Integer.toString(gye.getTargetMoney()));
                    } else if (gye.getType().equals("저축계")) {
                        GyeService.collectToken(mem.getEmail(), gye.getId(), Long.toString(targetMonthFee));
                    }

                    //낙찰계

                    if (gye.getType().equals("낙찰계")) {
                        GyeService.collectToken(mem.getEmail(), gye.getId(), Long.toString(targetMonthFee));
                        if(mem.getTurn() == nowTurn){
                            sendtoken_email = mem.getEmail();
                        }

                        target_month_money +=  targetMonthFee;

                    }
                }

                if (gye.getType().equals("낙찰계")) {

                    GyeService.sendToken(gye.getId(), sendtoken_email, String.valueOf(target_month_money));
                }

                if (gye.getPeriod() <= nowTurn) {
                    LocalDateTime expiredDay = LocalDateTime.now();
                    String strDate = expiredDay.toString();
                    System.out.println("the gye ended at : " + strDate);

                    GyeService.updateGye(gye.getId(), "expired", gye.getPayDay().toString());
                }
            }

            //  5. 계의 상태가 expired 라면 별도 처리 없음
            else if (state.equals("expired")) {
                continue;
            }
    }

}

    // 고정된 속도로 작업 예약
    @Scheduled(fixedRate = 10000)
    public void scheduleFixedRateTask() {

        System.out.println("Fixed rate task - " + System.currentTimeMillis() / 1000);
        CronJob();
    }

}
