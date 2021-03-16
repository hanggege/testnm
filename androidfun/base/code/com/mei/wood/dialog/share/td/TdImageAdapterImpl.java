package com.mei.wood.dialog.share.td;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.webkit.URLUtil;

import com.joker.support.TdAction;
import com.joker.support.listener.TdImageAdapter;
import com.mei.orc.ext.DimensionsKt;
import com.mei.orc.ui.loading.LoadingToggle;
import com.mei.wood.GlideApp;
import com.mei.wood.R;

;


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/3.
 */

public class TdImageAdapterImpl implements TdImageAdapter {

    private Activity activity;

    public TdImageAdapterImpl(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getImgBitmap(final Context context, final String url, final TdAction<Bitmap> callback) {
        if (activity instanceof LoadingToggle) {
            ((LoadingToggle) activity).showLoading(true);
        }
        new AsyncTask<String, String, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                if (URLUtil.isNetworkUrl(url)) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = GlideApp.with(context)
                                .asBitmap()
                                .load(url)
                                .error(R.drawable.knack_share_img)
                                .thumbnail(0.8f)
                                .submit(DimensionsKt.getScreenWidth(), DimensionsKt.getScreenHeight())
                                .get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return bitmap;
                } else {
                    String base64Url = url.contains(",") ? (url.substring(url.indexOf(",") + 1).replaceAll(" ", "+")) : url;
                    byte[] encode = Base64.decode(base64Url, Base64.DEFAULT);
                    return BitmapFactory.decodeByteArray(encode, 0, encode.length);
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (activity instanceof LoadingToggle) {
                    ((LoadingToggle) activity).showLoading(false);
                }
                callback.onCallback(bitmap);
            }
        }.execute();
    }

}
