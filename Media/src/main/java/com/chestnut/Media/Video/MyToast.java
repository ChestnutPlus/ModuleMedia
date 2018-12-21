package com.chestnut.Media.Video;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chestnut.Media.R;
import com.chestnut.common.ui.XToast;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/6/17 15:36
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */

public class MyToast {

    private XToast toast;
    private TextView textView;
    private ImageView icon;

    public MyToast(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_music_layout,null);
        toast = new XToast(context,view,Gravity.CENTER, Toast.LENGTH_LONG);
        textView = (TextView) view.findViewById(R.id.textView);
        icon = (ImageView) view.findViewById(R.id.icon);
    }

    public MyToast setIcon(@DrawableRes int res) {
        icon.setBackgroundResource(res);
        return this;
    }

    public MyToast setTxt(int a) {
        if (a<=0) {
            textView.setText("0%");
        }
        else if (a>=100) {
            textView.setText("100%");
        }
        else {
            textView.setText(a+"%");
        }
        return this;
    }

    public MyToast setTxt(@NonNull String s) {
        textView.setText(s);
        return this;
    }

    public void show() {
        toast.show();
    }
}
