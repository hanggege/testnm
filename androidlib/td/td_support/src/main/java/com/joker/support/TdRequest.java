package com.joker.support;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public class TdRequest {
    @SuppressLint("StaticFieldLeak")
    public static void requestNet(final String url, final TdAction<String> callBack) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    return TdNet.getStr(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callBack.onCallback(s);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @SuppressLint("StaticFieldLeak")
    public static void getBitmap(final String url, final TdAction<Bitmap> callback) {
        new AsyncTask<String, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                BufferedInputStream bufferedInputStream = null;
                InputStream inputStream = null;
                try {
                    bufferedInputStream = new BufferedInputStream(TdNet.getStream(url));
                    bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bufferedInputStream.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                callback.onCallback(bitmap);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @SuppressLint("StaticFieldLeak")
    public static void getByte(final String url, final TdAction<byte[]> callback) {
        new AsyncTask<String, Object, byte[]>() {
            @Override
            protected byte[] doInBackground(String... params) {
                byte[] bytes = null;
                try {
                    bytes = parseByte(TdNet.getStream(url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bytes;
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                super.onPostExecute(bytes);
                callback.onCallback(bytes);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    private static byte[] parseByte(InputStream inputStream) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            byte[] b = new byte[1024];
            int bytesRead = inputStream.read(b);
            while (bytesRead != -1) {
                bos.write(b, 0, bytesRead);
                bytesRead = inputStream.read(b);
            }
            bytes = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
}
