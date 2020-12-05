package com.ds.feige.im.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.base.nodepencies.strategy.id.IdKeyGeneratorFactory;

public class IdTimeTest {
    public static void main(String[] args) throws Exception {
        IdKeyGenerator<Long> keyGeneratorForLong =
            IdKeyGeneratorFactory.instance(IdKeyGeneratorFactory.IdType.WITH_OUT_DATE_LONG);
        long id1 = 1999999999999999999L;
        long id2 = 403953530927889408L;

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 10, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long epoch = calendar.getTimeInMillis();
        System.out.println("id1:" + Long.toBinaryString(id1));
        System.out.println("id2:" + Long.toBinaryString(id2));
        long timeDuration1 = id1 >> 22;
        long timeDuration2 = id2 >> 22;
        System.out.println("epoch:" + epoch);
        // IdUtil.createSnowflake()；
        System.out
            .println("Time1:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeDuration1 + epoch)));
        System.out
            .println("Time2:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeDuration2 + epoch)));
    }

}
