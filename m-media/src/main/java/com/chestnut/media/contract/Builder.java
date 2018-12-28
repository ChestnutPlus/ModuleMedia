package com.chestnut.media.contract;

import android.content.Context;
import android.graphics.Typeface;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/25 16:26
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class Builder {
    public Typeface typeface = null;//字体
    public Context context;//上下文
    public String url = null;//文件地址或者是链接
    public boolean isAutoPlay = false;
    public String title = null;
}
