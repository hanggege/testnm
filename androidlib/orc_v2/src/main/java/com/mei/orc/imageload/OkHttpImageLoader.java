package com.mei.orc.imageload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.URLUtil;

import com.mei.orc.http.retrofit.RetrofitClient;
import com.mei.orc.util.handler.GlobalUIHandler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/15
 */
public class OkHttpImageLoader {

    public static void loadImageAsBitmap(RetrofitClient client, String url, final com.mei.orc.callback.Callback<Bitmap> callback) {
        if (URLUtil.isNetworkUrl(url)) {
            Request request = new Request.Builder().url(url).build();
            client.getOkClient().newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("OkHttpImageLoader", "onFailure: 获取图片失败");
                            GlobalUIHandler.postUi(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onCallback(null);
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            Bitmap bitmap = null;
                            try {
                                ResponseBody body = response.body();
                                if (body != null) {
                                    bitmap = BitmapFactory.decodeStream(body.byteStream());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            final Bitmap finalBitmap = bitmap;
                            GlobalUIHandler.postUi(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onCallback(finalBitmap);
                                }
                            });
                        }
                    });
        } else {
            GlobalUIHandler.postUi(new Runnable() {
                @Override
                public void run() {
                    callback.onCallback(null);
                }
            });
        }
    }
}
