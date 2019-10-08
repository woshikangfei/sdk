package com.analytics.sdk.common.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateHelper {

    static final String TAG = DateHelper.class.getSimpleName();

    static final SimpleDateFormat dateHourFormater = new SimpleDateFormat("yyyy-MM-dd HH");
    static final SimpleDateFormat datetimeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
    static final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");

    public static String currentDate(){
        Date curDate = new Date(System.currentTimeMillis());
        String str = dateFormater.format(curDate);
        return str;
    }

    public static String currentDateHour(){
        Date curDate = new Date(System.currentTimeMillis());
        String str = dateHourFormater.format(curDate);
        return str;
    }

    public static String currentDatetime(){
        Date curDate = new Date(System.currentTimeMillis());
        String str = datetimeFormater.format(curDate);
        return str;
    }

    public static long diffHour(String lastDate,String current) {
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 分钟的毫秒数

        long day = 0;
        long hour = 0;
        long minute = 0;
        try {
            long diff = dateFormater.parse(current).getTime() - dateFormater.parse(lastDate).getTime();
            day = diff / nd;// 差多少天
            hour = diff / nh;
            minute = diff / nm;
            return hour;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hour;

    }

    public static String addCurrentDate(int intervalDays){
        return addDate(new Date(System.currentTimeMillis()),intervalDays);
    }

    public static String addDate(String date,int intervalDays){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return addDate(nowDate,intervalDays);
    }

    public static String addDate(Date date,int intervalDays){
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(date.getTime() + (long)intervalDays * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

}
