package com.mei.orc.util.date;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Created by Ling on 2019-08-01
 */
@StringDef({
        DateFormatType.DateFormatYMdHms,
        DateFormatType.DateFormatYMd,
        DateFormatType.DateFormatYMd2,
        DateFormatType.DateFormatYM,
        DateFormatType.DateFormatMD,
        DateFormatType.DateFormatMd,
        DateFormatType.DateFormatMDHm,
        DateFormatType.DateFormatMdDHm,
        DateFormatType.DateFormatHm,
        DateFormatType.DateFormatYMdDot,
        DateFormatType.DateFormatMdDot})
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface DateFormatType {
    String DateFormatYMdHms = "yyyy-MM-dd HH:mm:ss";
    String DateFormatYMd = "yyyy-MM-dd";
    String DateFormatYMd2 = "yyyyMMdd";
    String DateFormatYM = "yyyy年MM月";
    String DateFormatMD = "MM月dd日";
    String DateFormatMd = "MM-dd";
    String DateFormatMDHm = "MM月dd日 HH:mm";
    String DateFormatMdDHm = "MM-dd HH:mm";
    String DateFormatHm = "HH:mm";
    String DateFormatYMdDot = "yyyy.MM.dd";
    String DateFormatMdDot = "MM.dd";
}