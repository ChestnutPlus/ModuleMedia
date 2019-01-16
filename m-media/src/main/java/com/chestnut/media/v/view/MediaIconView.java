package com.chestnut.media.v.view;

import android.support.annotation.NonNull;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chestnut.media.contract.IMediaIcon;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2019/1/16 11:02
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class MediaIconView implements IMediaIcon<MediaIconView> {

    private LinearLayout ll;
    private ImageView icon;
    private TextView tv;
    private boolean isShowing = false;

    public MediaIconView(LinearLayout ll, ImageView icon, TextView tv) {
        this.ll = ll;
        this.icon = icon;
        this.tv = tv;
    }

    @Override
    public MediaIconView setIcon(int res) {
        icon.setImageResource(res);
        return this;
    }

    @Override
    public MediaIconView setTxt(int a) {
        if (a<=0) {
            tv.setText("0%");
        }
        else if (a>=100) {
            tv.setText("100%");
        }
        else {
            tv.setText(a+"%");
        }
        return this;
    }

    @Override
    public MediaIconView setTxt(@NonNull String s) {
        tv.setText(s);
        return this;
    }

    @Override
    public void dismiss() {
        if (isShowing) {
            isShowing = false;
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(1500);
            ll.startAnimation(alphaAnimation);
        }
    }

    @Override
    public void show() {
        if (!isShowing) {
            isShowing = true;
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setDuration(200);
            ll.startAnimation(alphaAnimation);
        }
    }
}
