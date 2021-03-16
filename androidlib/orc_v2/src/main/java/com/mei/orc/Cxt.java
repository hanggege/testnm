package com.mei.orc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by guoyufeng on 30/4/15.
 */
public class Cxt {

    @SuppressLint("StaticFieldLeak")
    private static Context _cxt;

    private static Resources _res;

    public static void set(Context context) {
        Cxt._cxt = context.getApplicationContext();
    }

    public static Context get() {
        return _cxt;
    }

    public static Resources getRes() {
        if (_res == null) {
            _res = _cxt.getResources();
        }
        return _res;
    }

    public static String getStr(int resId) {
        return _cxt.getString(resId);
    }

    public static String getStr(int resId, Object... fmtArgs) {
        return _cxt.getString(resId, fmtArgs);
    }

    public static int getColor(int colorId) {
        return getRes().getColor(colorId);
    }
}
