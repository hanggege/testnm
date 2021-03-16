package com.mei.orc.unit;

/**
 * Created by guoyufeng on 2/7/15.
 */
public interface TimeUnit {
    long SECOND = 1000;
    long MINUTE = SECOND * 60;
    long HOUR = MINUTE * 60;
    long DAY = HOUR * 24;
    long YEAR = DAY * 365;
    long MONTH = DAY * 30;
}
