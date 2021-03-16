package com.mei.wood.web;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import com.mei.orc.rxpermission.JokerPermissionKt;
import com.mei.orc.ui.toast.UIToast;

/**
 * @author caowei
 * @email 646030315@qq.com
 * @time Created on 2016/12/27 0027
 * @描述 用于webview上传文件，如图片
 */

@SuppressWarnings("unused")
public class OpenFileWebChromeClient extends WebChromeClient {
    private ValueCallback<Uri> mFilePathCallback;
    private ValueCallback<Uri[]> mFilePathCallbacks;
    private ValueCallback mFilePathCallback4;
    FragmentActivity mContext;

    public OpenFileWebChromeClient(FragmentActivity mContext) {
        super();
        this.mContext = mContext;
    }

    // Android < 3.0 调用这个方法
    public void openFileChooser(ValueCallback<Uri> filePathCallback) {
        mFilePathCallback = filePathCallback;
        openFiles();

    }

    // 3.0 + 调用这个方法
    public void openFileChooser(ValueCallback filePathCallback,
                                String acceptType) {
        mFilePathCallback4 = filePathCallback;
        openFiles();
    }

    // / js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
    // Android > 4.1.1 调用这个方法
    public void openFileChooser(ValueCallback<Uri> filePathCallback,
                                String acceptType, String capture) {
        mFilePathCallback = filePathCallback;
        openFiles();
    }

    @Override
    public boolean onShowFileChooser(WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        mFilePathCallbacks = filePathCallback;
        openFiles();
        return true;
    }

    private void openFiles() {
        JokerPermissionKt.requestSinglePermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, pair -> {
            if (pair.getFirst()) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                mContext.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    onActivityResult(result.getResultCode(), result.getData());
                });
            } else {
                mFilePathCallbacks = null;
                mFilePathCallback4 = null;
                mFilePathCallback = null;
                UIToast.toast(mContext, "需要文件读写权限才能用哦");
            }
            return null;
        });

    }


    /**
     * webView上传文件
     */
    public boolean onActivityResult(int resultCode, Intent data) {
        Uri uri = data != null ? data.getData() : null;
        if (mFilePathCallbacks != null) {
            mFilePathCallbacks.onReceiveValue(uri != null ? new Uri[]{uri} : null);
            return true;
        } else if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(uri);
            return true;
        } else if (mFilePathCallback4 != null) {
            mFilePathCallback4.onReceiveValue(uri);
            return true;
        }
        mFilePathCallback = null;
        mFilePathCallback4 = null;
        mFilePathCallbacks = null;
        return false;
    }

}
