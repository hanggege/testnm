package com.joker.qq.share;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.joker.TdType;
import com.joker.connect.TdCallBack;
import com.joker.constant.TdErrorCode;
import com.joker.flag.TdFlags;
import com.joker.model.BackResult;
import com.joker.model.ErrResult;
import com.joker.model.ShareInfo;
import com.joker.qq.R;
import com.joker.share.ShareModel;
import com.joker.support.TdSdk;
import com.joker.support.listener.TdPerFormSuper;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */
@SuppressLint("unused")
public class QQShareModel extends ShareModel implements TdPerFormSuper, IUiListener {

    private Tencent tencent;

    public Tencent getTencent() {
        if (tencent == null) {
            tencent = Tencent.createInstance(TdSdk.QQAppid(activity), activity);
        }
        return tencent;
    }

    public QQShareModel(@TdFlags Integer type, Activity activity, ShareInfo info, TdCallBack callBack) {
        super(type, activity, info, callBack);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
    }


    @Override
    public void perform() {
        sendShare(shareInfo);
    }

    @Override
    public void onComplete(Object o) {
        String json = o != null ? String.valueOf(o) : "";
        int ret = getInt(json, "ret");
        if (TextUtils.isEmpty(json)
                && ret != 0) {
            callBack.onFailure(new ErrResult(type, TdErrorCode.DATA_IS_ERR, "分享失败"));
        } else {
            callBack.onSuccess(new BackResult(type));
        }
    }

    @Override
    public void onError(UiError uiError) {
        callBack.onFailure(new ErrResult(type, uiError.errorCode, uiError.errorMessage));
        unregisterLifecycle();
    }

    @Override
    public void onCancel() {
        callBack.onFailure(new ErrResult(type, TdErrorCode.USER_CANCEL_ERR, "分享失败，取消分享"));
        unregisterLifecycle();
    }


    private void sendShare(ShareInfo shareInfo) {
        Bundle params = new Bundle();
        if (shareInfo.shareType == TdType.image_share) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareInfo.imgUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, activity.getString(R.string.app_name));
            if (shareInfo.clientType == TdType.friend_circle) {
                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
            }
            getTencent().shareToQQ(activity, params, this);
        } else if (shareInfo.clientType == TdType.we_chat) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.title);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.content);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.shareUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, activity.getString(R.string.app_name));
            params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.imgUrl);
            getTencent().shareToQQ(activity, params, this);
        } else {
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareInfo.title);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareInfo.content);
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareInfo.shareUrl);
            ArrayList<String> list = new ArrayList<String>();
            list.add(shareInfo.imgUrl);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list);
            getTencent().shareToQzone(activity, params, this);
        }

    }

    private static int getInt(String result, String key) {
        int value = -1;
        try {
            JSONObject json = new JSONObject(result);
            value = json.getInt(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
