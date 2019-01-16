package com.chestnut.media.contract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import com.chestnut.media.v.activity.VideoActivity;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/25 16:33
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class VideoBuilder extends Builder{

    VideoBuilder() {}
    public boolean isLoop = false;
    public boolean isShowControlView = true;

    public VideoBuilder setTypeface(Typeface typeface) {
        this.typeface = typeface;
        return this;
    }

    public VideoBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public VideoBuilder setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
        return this;
    }

    public VideoBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public VideoBuilder setLoop(boolean loop) {
        isLoop = loop;
        return this;
    }

    public VideoBuilder setShowControlView(boolean showControlView) {
        isShowControlView = showControlView;
        return this;
    }

    public VideoBuilder setCacheAble(boolean cacheAble) {
        this.cacheAble = cacheAble;
        return this;
    }

    public void build(Context context) {
        this.context = context;
        long key = MediaManager.getInstance().push(this);
        Intent intent = new Intent(context, VideoActivity.class);
        if (!(context instanceof Activity))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MediaManager.Key_Builder, key);
        context.startActivity(intent);
    }

    public VideoBuilder buildNoView(Context context) {
        this.context = context;
        return this;
    }
}
