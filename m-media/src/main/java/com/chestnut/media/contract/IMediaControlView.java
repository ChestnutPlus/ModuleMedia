package com.chestnut.media.contract;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2019/1/16 11:36
 *     desc  :  控制播放控制View的隐藏和出现
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public interface IMediaControlView {
    void onDown();
    void onUp();
    void onSetBuilder(boolean isShowControlView);
    void onControlViewClick();
    void onSeekBarStartTrackingTouch();
    void onSeekBarStopTrackingTouch();
}
