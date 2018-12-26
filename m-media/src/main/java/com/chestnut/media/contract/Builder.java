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
public abstract class Builder {

    public Typeface typeface = null;//字体
    public Context context;//上下文
    public String url = null;//文件地址或者是链接
    public boolean isAutoPlay = false;
    public String title = null;

    Builder() {}

    public void build(Context context) {
        this.context = context;
        MediaManager.getInstance().execute(this);
    }

    public Builder setTypeface(Typeface typeface) {
        this.typeface = typeface;
        return this;
    }

    public Builder setUrl(String url) {
        this.url = url;
        return this;
    }

    public Builder setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
        return this;
    }

    public Builder setTitle(String title) {
        this.title = title;
        return this;
    }
}
