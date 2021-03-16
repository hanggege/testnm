package com.mei.faceunity.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.mei.widget.round.RoundTextView;
import com.mei.wood.R;


/**
 * Created by tujh on 2018/6/28.
 */
public final class ToastUtil {
    private static Toast sNormalToast;
    private static Toast sFineToast;
    private static Toast sWhiteTextToast;


    public static void showToast(Context context, int str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static void showNormalToast(Context context, @StringRes int strId) {
        String text = context.getString(strId);
        if (sNormalToast == null) {
            context = context.getApplicationContext();
            Resources resources = context.getResources();
            RoundTextView textView = new RoundTextView(context);
            textView.setTextColor(resources.getColor(R.color.colorWhite));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.x26));
            textView.setBackColor(ContextCompat.getColor(context, R.color.color_BD050F14));
            textView.setRadius(context.getResources().getDimensionPixelOffset(R.dimen.x8));
            int hPadding = resources.getDimensionPixelSize(R.dimen.x28);
            int vPadding = resources.getDimensionPixelSize(R.dimen.x16);
            textView.setPadding(hPadding, vPadding, hPadding, vPadding);
            textView.setText(text);
            sNormalToast = new Toast(context);
            sNormalToast.setView(textView);
            sNormalToast.setDuration(Toast.LENGTH_SHORT);
            int yOffset = context.getResources().getDimensionPixelSize(R.dimen.x582);
            sNormalToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, yOffset);
            sNormalToast.show();
        } else {
            TextView textView = (TextView) sNormalToast.getView();
            textView.setText(text);
            if (!textView.isShown()) {
                sNormalToast.show();
            }
        }
    }


}
