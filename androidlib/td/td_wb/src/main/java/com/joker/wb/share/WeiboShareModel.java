package com.joker.wb.share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.joker.TdType;
import com.joker.connect.TdCallBack;
import com.joker.constant.TdErrorCode;
import com.joker.flag.TdFlags;
import com.joker.model.BackResult;
import com.joker.model.ErrResult;
import com.joker.model.ShareInfo;
import com.joker.share.ShareModel;
import com.joker.support.TdAction;
import com.joker.support.TdRequest;
import com.joker.support.listener.TdImageAdapter;
import com.joker.support.listener.TdPerFormSuper;
import com.joker.utils.TdUtil;
import com.joker.wb.WeiboManager;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

@SuppressWarnings("unused")
class WeiboShareModel extends ShareModel implements TdPerFormSuper, WbShareCallback {

    private WbShareHandler shareHandler;
    private TdImageAdapter adapter;

    private WbShareHandler getshareHandler(Activity activity) {
        if (shareHandler == null) {
            shareHandler = new WbShareHandler(activity);
            shareHandler.registerApp();
        }
        return shareHandler;
    }

    public WeiboShareModel(@TdFlags Integer type, Activity activity, @NonNull ShareInfo info, @NonNull TdCallBack callBack, TdImageAdapter adapter) {
        super(type, activity, info, callBack);
        WeiboManager.weiboInit(activity);
        this.adapter = adapter;
    }

    @Override
    public void perform() {
        getMulMsg(new TdAction<WeiboMultiMessage>() {
            @Override
            public void onCallback(WeiboMultiMessage weiboMultiMessage) {
                getshareHandler(activity).shareMessage(weiboMultiMessage, false);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (shareHandler != null) shareHandler.doResultIntent(intent, this);
    }

    @Override
    public void onWbShareSuccess() {
        callBack.onSuccess(new BackResult(type));
        unregisterLifecycle();
    }

    @Override
    public void onWbShareCancel() {
        callBack.onFailure(new ErrResult(type, TdErrorCode.USER_CANCEL_ERR, "取消分享"));
        unregisterLifecycle();
    }

    @Override
    public void onWbShareFail() {
        callBack.onFailure(new ErrResult(type, TdErrorCode.NONE_CODE_ERR, "分享失败"));
        unregisterLifecycle();
    }

    private void getMulMsg(final TdAction<WeiboMultiMessage> back) {
        if (URLUtil.isNetworkUrl(shareInfo.imgUrl) || shareInfo.imgUrl.startsWith("data:image/png;base64,")) {
            getBitmap(shareInfo.imgUrl, shareInfo.resID, new TdAction<Bitmap>() {
                @Override
                public void onCallback(Bitmap bitmap) {
                    if (bitmap != null) {
                        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                        if (shareInfo.shareType != TdType.image_share) {
                            weiboMessage.textObject = getTextObj();
                        }
                        weiboMessage.imageObject = getImageObj(bitmap);
                        back.onCallback(weiboMessage);
                    } else {
                        callBack.onFailure(new ErrResult(type, TdErrorCode.IMAGE_DECODE_ERR, "分享失败，请重试"));
                    }
                }
            });
        } else {
            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
            if (shareInfo.shareType != TdType.image_share) {
                weiboMessage.textObject = getTextObj();
            }
            weiboMessage.imageObject = getImageObj(shareInfo.imgUrl);
            back.onCallback(weiboMessage);
        }

    }


    private void getBitmap(String url, @DrawableRes int res, final TdAction<Bitmap> back) {
        if (TextUtils.isEmpty(url)) {
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), res);
            back.onCallback(bitmap);
        } else {
            if (adapter != null) {
                adapter.getImgBitmap(activity, url, back);
            } else {
                TdRequest.getBitmap(url, back);
            }
        }
    }


    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        String input = shareInfo.title + "\n" + shareInfo.content;
        String defaultEnd = shareInfo.defaultEnd.startsWith("@") ? shareInfo.defaultEnd : TdUtil.concate("@", shareInfo.defaultEnd);
        textObject.text = TdUtil.concate(input, input.contains(defaultEnd) ? "" : "" + defaultEnd + " ", shareInfo.shareUrl);
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Bitmap img) {
//        Bitmap bitmap = TdUtil.ImageCrop(img);
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(img);
        return imageObject;
    }

    private ImageObject getImageObj(String filePath) {
        ImageObject imageObject = new ImageObject();
        imageObject.imagePath = filePath;
        return imageObject;
    }


}
