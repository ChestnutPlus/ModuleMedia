package com.chestnut.media.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/6/18 20:48
 *     desc  :  亮度调节管理者：
 *              只是调节App层面的亮度，而非系统层。
 *              后面会归入到：ScreenUtils中。
 *     thanks To:   http://www.cnblogs.com/lwbqqyumidi/p/4127012.html
 *     dependent on:
 *     update log:
 * </pre>
 */

public class LightUtils {

    private static int STEP_LIGHT_100 = 2;  //以0-100为调节范围的，亮度调节步进值。

    /**
     * 设置亮度步进值
     * @param stepValue  0 - 100
     */
    public static void setStepLight100(int stepValue) {
        STEP_LIGHT_100 = stepValue;
    }

    public static int addLight100(Activity activity) {
        int a = getAppLight100(activity)+STEP_LIGHT_100;
        a = a >= 100 ? 100 : a;
        return setAppLight100(activity,a);
    }

    public static int subLight100(Activity activity) {
        int a = getAppLight100(activity)-STEP_LIGHT_100;
        a = a <= 0 ? 0 : a;
        return setAppLight100(activity,a);
    }

    /**
     * 以1-100为散列，设置App层面的亮度
     * @param activity
     * @param paramInt  0 - 100
     */
    public static int setAppLight100(Activity activity ,int paramInt){
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        localLayoutParams.screenBrightness = paramInt * 0.01f;
        localWindow.setAttributes(localLayoutParams);
        return getAppLight100(activity);
    }

    /**
     * 以1-100为散列，获取App层面的亮度
     * @param activity
     * @return  1-100
     */
    public static int getAppLight100(Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        int result = (int) (localLayoutParams.screenBrightness * 100);
        result = result<0 ? getScreenBrightness100(activity) : result;
        return result;
    }

    /**
     * 获取当前屏幕的亮度
     * @param context
     * @return
     */
    private static int getScreenBrightness100(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            //这里获取到的是：1-225的亮度
            nowBrightnessValue = Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return (int) (nowBrightnessValue*1.0/2.25);
    }
}