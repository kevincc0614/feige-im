package com.ds.feige.im.test.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.base.nodepencies.strategy.id.IdKeyGeneratorFactory;

/**
 * @author DC
 */
public class IdUtils {
    public static void main(String[] args) {
        IdKeyGenerator<Long> keyGeneratorForLong =
            IdKeyGeneratorFactory.instance(IdKeyGeneratorFactory.IdType.WITH_OUT_DATE_LONG);
        long id = keyGeneratorForLong.generateId();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 10, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long epoch = calendar.getTimeInMillis();
        long timeDuration = id >> 22;
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeDuration + epoch)));
    }
}
