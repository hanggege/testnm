package com.joker.connect;

import android.content.Context;

import androidx.annotation.NonNull;

import com.joker.model.BackResult;
import com.joker.model.TdUserInfo;
import com.joker.support.TdAction;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public interface TdConstants {
    String QQ_PACKAGE = "com.joker.qq";
    String QQ_MANAGER_IMPL = QQ_PACKAGE + ".QQManagerImpl";
    String QQ_LOGIN_MODEL = QQ_PACKAGE + ".login.QQLoginModel";
    String QQ_SHARE_MODEL = QQ_PACKAGE + ".share.QQShareModel";


    String WEIBO_PACKAGE = "com.joker.wb";
    String WEIBO_LOGIN_MODEL = WEIBO_PACKAGE + ".login.WeiboLoginModel";
    String WEIBO_SHARE_MODEL = WEIBO_PACKAGE + ".share.WeiboShareModel";

    void getUserInfo(Context context, @NonNull BackResult result, TdAction<TdUserInfo> callback);
}
