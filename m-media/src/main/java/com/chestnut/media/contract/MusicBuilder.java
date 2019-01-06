package com.chestnut.media.contract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import com.chestnut.media.v.activity.MusicDialogActivity;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2018/12/27 22:40
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */

public class MusicBuilder extends Builder{

    public Callback callback;

    MusicBuilder() {}

    public MusicBuilder setTypeface(Typeface typeface) {
        this.typeface = typeface;
        return this;
    }

    public MusicBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public MusicBuilder setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
        return this;
    }

    public MusicBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public MusicBuilder setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public MusicBuilder buildNoView(Context context) {
        this.context = context;
        return this;
    }

    public void build(Context context) {
        this.context = context;
        long key = MediaManager.getInstance().push(this);
        Intent intent = new Intent(context, MusicDialogActivity.class);
        if (!(context instanceof Activity))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MediaManager.Key_Builder, key);
        context.startActivity(intent);
    }

    public interface Callback {
        void onBuffering(int percent);
        void onSeekTo(int seconds);
        void onStart();
        void onStop();
        void onCompletion();
        void onPause();
        void onErr(int code, String msg);
        void onProgressChange(int currentSecond, int totalSecond);
    }
}
