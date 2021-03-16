package com.joker.share;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.joker.TdType;
import com.joker.connect.TdCallBack;
import com.joker.connect.TdConstants;
import com.joker.flag.TdFlags;
import com.joker.model.ShareInfo;
import com.joker.support.listener.TdImageAdapter;
import com.joker.support.listener.TdPerFormSuper;
import com.joker.support.proxy.TransitionFactory;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public class TdShareHelper {

    private Activity activity;
    private TdImageAdapter imageAdapter;


    public TdShareHelper(@NonNull Activity activity) {
        this.activity = activity;
    }

    public TdShareHelper(Activity activity, TdImageAdapter imageAdapter) {
        this.activity = activity;
        this.imageAdapter = imageAdapter;
    }

    public synchronized boolean performShare(@TdFlags int type, @NonNull ShareInfo info, @NonNull TdCallBack callBack) {
        if (activity == null) {//未初始化
            Log.e("TdLoginHelper", "TdLoginHelper未初始化");
            return false;
        }
        TdPerFormSuper shareModel = null;
        switch (type) {
            case TdType.weibo:
                TransitionFactory.RfParams wbParams = new TransitionFactory.RfParams();
                wbParams.append(Integer.class, type);
                wbParams.append(Activity.class, activity);
                wbParams.append(ShareInfo.class, info);
                wbParams.append(TdCallBack.class, callBack);
                wbParams.append(TdImageAdapter.class, imageAdapter);
                shareModel = TransitionFactory.create(TdConstants.WEIBO_SHARE_MODEL, wbParams);
//                shareModel = new WeiboShareModel(type, activity, info, callBack, imageAdapter);
                break;
            case TdType.weixin:
                shareModel = new WeixinShareModel(type, activity, info, callBack, imageAdapter);
                break;
            case TdType.qq:
                TransitionFactory.RfParams qqParams = new TransitionFactory.RfParams();
                qqParams.append(Integer.class, type);
                qqParams.append(Activity.class, activity);
                qqParams.append(ShareInfo.class, info);
                qqParams.append(TdCallBack.class, callBack);
                shareModel = TransitionFactory.create(TdConstants.QQ_SHARE_MODEL, qqParams);
                break;
            default:
                break;
        }
        if (shareModel == null) {//未支持到
            Log.e("TdLoginHelper", "TdLoginHelper未支持到");
            return false;
        }
        shareModel.perform();
        return true;
    }

}
