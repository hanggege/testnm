package com.mei.orc.ui.toast;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.didiglobal.booster.instrument.ShadowToast;
import com.mei.orc.Cxt;
import com.mei.orc.R;


/**
 * Created by Joker on 2015/6/26.
 */
public class UIToast {

    public static void toast(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT, Type.center);
    }

//    public static void toast(Context context, String msg, int length) {
//        show(context, msg, length, Type.center);
//    }

    //自定义类型L中间/底部
    public static void toast(Context context, String msg, Type type) {
        show(context, msg, Toast.LENGTH_SHORT, type);
    }

    //自定义类型L中间/底部
    public static void toast(Context context, @StringRes int resMsg, Type type) {
        String msg = "";
        if (context != null) {
            msg = context.getString(resMsg);
        }
        show(context, msg, Toast.LENGTH_SHORT, type);
    }

    public static void toast(String msg) {
        show(Cxt.get(), msg, Toast.LENGTH_SHORT, Type.center);
    }

    public static void toast(@StringRes int resMsg) {
        String msg = Cxt.getStr(resMsg);
        show(Cxt.get(), msg, Toast.LENGTH_SHORT, Type.center);
    }

    public static void toast(Context context, @StringRes int resMsg) {
        String msg = "";
        if (context != null) {
            msg = context.getString(resMsg);
        }
        show(context, msg, Toast.LENGTH_SHORT, Type.center);
    }

//    public static void toast(Context context, String msg, int time, Type type) {
//        show(context, msg, time, type);
//    }


    private static void show(Context context, String msg, int time, Type type) {
        if (TextUtils.isEmpty(msg) || msg.contains("failed to") || context == null) {
            return;
        }
        Toast toast;
        if (isAtLeastR()) {
            toast = Toast.makeText(context, msg, time);
        } else {
            /** Andriod R之后不再支持自定义Toast的View **/
            toast = new Toast(context.getApplicationContext());
            toast.setView(getToastView(context.getApplicationContext()));

            toast.setDuration(time);
            ((TextView) toast.getView()).setText(msg);
            if (type == Type.center) {
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
        }

        ShadowToast.show(toast);
    }

    private static TextView getToastView(Context context) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setTextSize(14f);
        tv.setTextColor(Color.WHITE);
        tv.setPadding(dip(context, 14), dip(context, 10), dip(context, 14), dip(context, 10));
        tv.setBackgroundResource(R.drawable.toast_bg);
        return tv;
    }

    public enum Type {
        center, bottom
    }

    private static int dip(Context context, float value) {
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }


    private static boolean isAtLeastR() {
        return Build.VERSION.CODENAME.length() == 1
                && Build.VERSION.CODENAME.charAt(0) >= 'R'
                && Build.VERSION.CODENAME.charAt(0) <= 'Z';
    }

}
