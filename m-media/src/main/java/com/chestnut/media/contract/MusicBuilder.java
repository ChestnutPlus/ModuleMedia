package com.chestnut.media.contract;

import android.content.Context;

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

    public MusicPresenter buildNoView(Context context) {
        this.context = context;
        MusicPresenter musicPresenter = new MusicPresenter();
        musicPresenter.setBuilder(this);
        return musicPresenter;
    }
}
