package com.chestnut.Media.Video;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/6/2 23:03
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */

public class TimeUtils {

    public static String toMediaTime(int second) {
        int a;
        int b;
        a = second/3600;
        second -= a * 3600;
        b = second/60;
        second -= b * 60;
        if (a<=0) {
            return String.format("%02d",b)+":"+String.format("%02d",second);
        }
        else
            return a+":"+String.format("%02d",b)+":"+String.format("%02d",second);
    }
}
