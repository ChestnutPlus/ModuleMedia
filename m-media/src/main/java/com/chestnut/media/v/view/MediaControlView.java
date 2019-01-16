package com.chestnut.media.v.view;

import android.view.View;
import android.view.animation.TranslateAnimation;

import com.chestnut.media.contract.IMediaControlView;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2019/1/16 11:36
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class MediaControlView implements IMediaControlView {

    private View bottomView, topView;
    private boolean isShowView = false;

    public MediaControlView(View bottomView, View topView) {
        this.bottomView = bottomView;
        this.topView = topView;
    }

    @Override
    public void onDown() {
        showImmediately();
    }

    @Override
    public void onUp() {
        hide(800,5000);
    }

    @Override
    public void onSetBuilder(boolean isShowControlView) {
        if (!isShowControlView) {
            topView.setVisibility(View.INVISIBLE);
            bottomView.setVisibility(View.INVISIBLE);
        }
        isShowView = isShowControlView;
    }

    @Override
    public void onControlViewClick() {
        showImmediately();
        hide(800,5000);
    }

    @Override
    public void onSeekBarStartTrackingTouch() {
        showImmediately();
    }

    @Override
    public void onSeekBarStopTrackingTouch() {
        hide(800,5000);
    }

    private void hide(long durationTime, long offsetTime) {
        if (!isShowView)
            return;
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0,0,-topView.getHeight());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(durationTime);
        translateAnimation.setStartOffset(offsetTime);
        topView.startAnimation(translateAnimation);

        translateAnimation = new TranslateAnimation(0, 0,0,bottomView.getHeight());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(durationTime);
        translateAnimation.setStartOffset(offsetTime);
        bottomView.startAnimation(translateAnimation);
    }

    private void showImmediately() {
        if (!isShowView)
            return;
        topView.clearAnimation();
        bottomView.clearAnimation();
        topView.setY(0);
        bottomView.setY(bottomView.getY());
    }
}
