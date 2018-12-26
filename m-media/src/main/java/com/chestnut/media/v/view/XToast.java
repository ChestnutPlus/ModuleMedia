package com.chestnut.media.v.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <pre>
 *     author: Chestnut
 *     blog  :
 *     time  : 2016年10月20日16:28:09
 *     desc  : DIY Toast
 *     thanks To:
 *     dependent on:
 *     updateLog：
 *          1.0.0   基本功能。
 * </pre>
 */
public class XToast {

    private Toast toast;
    private LinearLayout toastView;         //Toast的根布局

    /**
     * 完全自定义布局Toast
     */
    public XToast() {}

    /**
     * 系统原生的布局Toast
     * @param context   上下文
     */
    public XToast(Context context, int duration){
        toast=Toast.makeText(context.getApplicationContext(),"",duration);
        toast.setDuration(duration);
        toastView = (LinearLayout) toast.getView();
    }

    /**
     * 完全自定义布局Toast
     * @param context   上下文
     * @param view  自定义 View
     * @param gravity 位置
     * @param duration 时长
     */
    public XToast(Context context, View view, int gravity, int duration){
        toast=new Toast(context.getApplicationContext());
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(gravity,0,0);
        toastView = (LinearLayout) toast.getView();
    }

    /**
     * 向Toast中添加自定义view
     * @param view  完全自定义的。
     * @param gravity 位置
     * @return  this
     */
    public XToast setView(View view, int gravity) {
        toast.setGravity(gravity,0,0);
        toast.setView(view);
        return this;
    }

    /**
     *      设置内容
     * @param message   设置内容
     * @return  this
     */
    public XToast setText(CharSequence message) {
        TextView textView=((TextView) toastView.findViewById(android.R.id.message));
        textView.setText(message);
        return this;
    }

    /**
     * 设置 字体
     * @param typeface typeface
     * @return this
     */
    public XToast setTextTypeface(Typeface typeface) {
        try {
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTypeface(typeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置 字体 大小
     * @param unit
     *          TypedValue.COMPLEX_UNIT_PX : Pixels
     *          TypedValue.COMPLEX_UNIT_SP : Scaled Pixels
     *          TypedValue.COMPLEX_UNIT_DIP : Device Independent Pixels
     * @param size  大小
     * @return this
     */
    public XToast setTextSize(int unit, int size) {
        try {
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextSize(unit,size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public XToast setTextSize(int size) {
        return setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
    }

    /**
     * 设置Toast字体及背景颜色
     * @param messageColor  文字颜色
     * @param backgroundColor   背景颜色
     * @return  this
     */
    public XToast setToastColor(int messageColor, int backgroundColor) {
        View view = toast.getView();
        if(view!=null){
            TextView message=((TextView) view.findViewById(android.R.id.message));
            message.setBackgroundColor(backgroundColor);
            message.setTextColor(messageColor);
        }
        return this;
    }

    /**
     * 设置Toast字体及背景
     * @param messageColor  字体颜色
     * @param background    背景资源地址，传入自定义xml地址
     * @return  this
     */
    public XToast setToastBackground(int messageColor, int background) {
        View view = toast.getView();
        view.setBackgroundColor(Color.TRANSPARENT);
        TextView message=((TextView) view.findViewById(android.R.id.message));
        message.setBackgroundResource(background);
        message.setTextColor(messageColor);
        return this;
    }

    /**
     * 短时间显示Toast
     * @param context   上下文
     * @param message   message
     */
    public XToast Short(Context context, CharSequence message){
        if(toast==null||(toastView!=null&&toastView.getChildCount()>1)){
            toast= Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toastView=null;
        }else{
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        return this;
    }

    /**
     * 短时间显示Toast
     * @param context   上下文
     * @param message   message
     */
    public XToast Short(Context context, int message) {
        if(toast==null||(toastView!=null&&toastView.getChildCount()>1)){
            toast= Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toastView=null;
        }else{
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        return this;
    }

    /**
     * 长时间显示Toast
     * @param context   上下文
     * @param message   message
     */
    public XToast Long(Context context, CharSequence message){
        if(toast==null||(toastView!=null&&toastView.getChildCount()>1)){
            toast= Toast.makeText(context, message, Toast.LENGTH_LONG);
            toastView=null;
        }else{
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        return this;
    }

    /**
     * 长时间显示Toast
     * @param context   上下文
     * @param message   message
     */
    public XToast Long(Context context, int message) {
        if(toast==null||(toastView!=null&&toastView.getChildCount()>1)){
            toast= Toast.makeText(context, message, Toast.LENGTH_LONG);
            toastView=null;
        }else{
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        return this;
    }

    /**
     * 自定义显示Toast时间
     * @param context   上下文
     * @param message   message
     * @param duration  时长
     */
    public XToast Indefinite(Context context, CharSequence message, int duration) {
        if(toast==null||(toastView!=null&&toastView.getChildCount()>1)){
            toast= Toast.makeText(context, message,duration);
            toastView=null;
        }else{
            toast.setText(message);
            toast.setDuration(duration);
        }
        return this;
    }

    /**
     * 自定义显示Toast时间
     * @param context   上下文
     * @param message   message
     * @param duration  时长
     */
    public XToast Indefinite(Context context, int message, int duration) {
        if(toast==null||(toastView!=null&&toastView.getChildCount()>1)){
            toast= Toast.makeText(context, message,duration);
            toastView=null;
        }else{
            toast.setText(message);
            toast.setDuration(duration);
        }
        return this;
    }

    /**
     * 显示 Toast
     * @return this
     */
    public XToast show() {
        if (toast!=null) {
            toast.show();
        }
        return this;
    }
}
