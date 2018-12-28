package com.chestnut.media.contract;

import android.content.Context;
import android.graphics.Typeface;

import com.chestnut.media.p.MusicPresenter;

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

    public MusicPresenter buildNoView(Context context) {
        this.context = context;
        MusicPresenter musicPresenter = new MusicPresenter(context);
        musicPresenter.setBuilder(this);
        return musicPresenter;
    }

    public void build(Context context) {

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
