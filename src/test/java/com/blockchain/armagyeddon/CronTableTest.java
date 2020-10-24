package com.blockchain.armagyeddon;

import org.junit.jupiter.api.Test;
import java.text.SimpleDateFormat;
import java.util.Date;


class CronTableTest {
    @Test
    public void dateFormantTest() {
        Date payDay =  new Date();
        System.out.println(payDay.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String strDate = sdf.format(payDay);
        System.out.println(strDate);

    }
}
