package com.mei.wood.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.mei.base.ui.nav.Nav;
import com.mei.orc.util.string.StringUtilKt;
import com.mei.wood.BuildConfig;

/**
 * Created by steven on 1/21/16.
 */
public abstract class MelkorWebViewClient extends WebViewClient {

    static String TAG = "MelkorWebViewClient";

    public abstract boolean handleUrlLoading(WebView view, @NonNull String url);

    public void handleLoadError() {

    }

    /**
     * 请用上面的方法处理 {@link #handleUrlLoading(WebView, String)}
     *
     * @return
     */
    @Override
    @Deprecated
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        url = changeScheme2(url);
        Log.e(TAG, "shouldOverrideUrlLoading: " + url);
        boolean hasIntercept = handleUrlLoading(view, StringUtilKt.nonNullOf(url));
        if (!hasIntercept && !URLUtil.isNetworkUrl(url)) {
            Nav.toUriActivity(view.getContext(), url);
            hasIntercept = true;
        }
        return hasIntercept;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.e(TAG, "onPageStarted: " + url);

    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.e(TAG, "onReceivedError old: " + description);
        if ((failingUrl != null && !failingUrl.equals(view.getUrl()) && !failingUrl.equals(view.getOriginalUrl()))
                || (failingUrl == null && errorCode != -12)
                || errorCode == -1) {
            return;
        }
        handleLoadError();
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        Log.e(TAG, "onReceivedHttpError: " + errorResponse.getStatusCode());
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        boolean intercept = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "onReceivedError M: " + error.getDescription().toString());
            if (error.getDescription().toString().contains("ERR_SPDY_COMPRESSION_ERROR")) {
                intercept = true;
            } else if (error.getDescription().toString().contains("ERR_SSL_PROTOCOL_ERROR")) {
                intercept = true;
            } else if (request.getUrl().toString().endsWith(".jpg")
                    || request.getUrl().toString().endsWith(".mp3")
                    || request.getUrl().toString().endsWith(".mp4")
                    || request.getUrl().toString().endsWith(".png")
                    || request.getUrl().toString().endsWith(".gif")
                    || request.getUrl().toString().endsWith(".js")
                    || request.getUrl().toString().endsWith(".css")
            ) {
                Log.e(TAG, "onReceivedError url: " + request.getUrl().toString());
                intercept = true;
            }
        } else {
            Log.e(TAG, "onReceivedError: " + view.getUrl());
        }
        if (!intercept) handleLoadError();
    }


    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//        super.onReceivedSslError(view, handler, error);
        handler.proceed();
        Log.e(TAG, "onReceivedSslError: " + view.getUrl());
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.e(TAG, "onPageFinished: " + url);

    }

    /**
     * 将xiaolu统一改成xiaolu2.0
     */
    public static String changeScheme2(String url) {
        // 兼容老内链(防止其它端口内链传错)
        if (!TextUtils.isEmpty(url)) {
            String appScheme = BuildConfig.APP_SCHEME + "://";
            url = url.replace("xiaolu://", appScheme)
                    .replace("xiaolu2.0://", appScheme)
                    .replace("aman://", appScheme)
                    .replace("aman2.0://", appScheme)
                    .replace("dove2.0://", appScheme);
        }
        return url;
    }


    public boolean isWebAppLink(String url) {
        return isAppLink(url);
    }

    public static boolean isAppLink(String url) {
        return !TextUtils.isEmpty(url) &&
                (url.startsWith("xiaolu://")
                        || url.startsWith("xiaolu2.0://")
                        || url.startsWith("aman://")
                        || url.startsWith("aman2.0://")
                        || url.startsWith("dove2.0://")
                        || url.startsWith(BuildConfig.APP_SCHEME)
                );
    }


}
