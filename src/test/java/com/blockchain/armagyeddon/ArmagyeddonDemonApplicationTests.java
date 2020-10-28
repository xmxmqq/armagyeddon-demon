package com.blockchain.armagyeddon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@SpringBootTest
class ArmagyeddonDemonApplicationTests {

    @Test
    void contextLoads() {
        LocalDateTime payDay = LocalDateTime.now();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String strDate = sdf.format(payDay);
        System.out.println("the gye starts at : " + strDate);
    }

}
