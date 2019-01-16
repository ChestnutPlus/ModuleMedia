package com.chestnut.media.contract;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2019/1/16 10:18
 *     desc  :  控制调节音量/亮度/快进快退的现实View
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public interface IMediaIcon<T> {
    T setIcon(@DrawableRes int res);
    T setTxt(int a);
    T setTxt(@NonNull String s);
    void dismiss();
    void show();
}
