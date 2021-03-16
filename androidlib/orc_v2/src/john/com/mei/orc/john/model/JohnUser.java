package com.mei.orc.john.model;

import android.content.Intent;
import android.text.TextUtils;

import com.mei.orc.Cxt;
import com.mei.orc.common.CommonConstant;
import com.mei.orc.util.save.SharePreKt;

import static com.mei.orc.util.device.DeviceUtilKt.get_phone_sn;

/**
 * Created by steven on 15/4/24.
 */
public class JohnUser {

    public int userID;
    private String sessionID = "";
    private String userName = "";
    private String push_client_id = "";

    public String deviceIMEI = "";
    public String phoneSN = "";

    private static volatile JohnUser mInstance;

    private JohnUser() {

    }


    public static JohnUser getSharedUser() {
        if (mInstance == null) {
            synchronized (JohnUser.class) {
                if (mInstance == null) {
                    mInstance = new JohnUser();
                }
            }
        }
        return mInstance;
    }

    public boolean hasLogin() {
        return getUserID() > 0 && !TextUtils.isEmpty((getSessionID()));
    }

    public String getUserIDAsString() {
        return userID == 0 ? "" : String.valueOf(userID);
    }

    public int getUserID() {
        return userID;
    }

    private void setUserID(int userID) {
        this.userID = userID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionId) {
        this.sessionID = sessionId;
        if (!TextUtils.isEmpty(sessionId)) {
            SharePreKt.putValue(CommonConstant.Key.SESSION_ID, sessionId);
            SharePreKt.putValue("key_login_img_show", true);
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLoginInfo(AccountLoginResult loginResult) {
        setLoginInfo(loginResult, true);
    }

    public void setLoginInfo(AccountLoginResult loginResult, boolean isGenderCheck) {
        if (loginResult != null) {
            setUserID(loginResult.login_user_id);
            setSessionID(loginResult.session_id);
            setUserName(loginResult.login_user_name);

            SharePreKt.putValue(CommonConstant.Key.LOGIN_USER_ID, loginResult.login_user_id);
            SharePreKt.putValue(CommonConstant.Key.SESSION_ID, loginResult.session_id);
            SharePreKt.putValue(CommonConstant.Key.LOCAL_USER_NAME, loginResult.login_user_name);
            if (isGenderCheck) {
                Cxt.get().sendBroadcast(
                        new Intent(CommonConstant.Action.LOGIN())
                                .putExtra(CommonConstant.Action.LOGIN_LOGOUT_EXTRA_USER_ID, loginResult.login_user_id)
                );
            }
        } else {
            int originalUserId = getUserID();

            setUserID(0);
            setSessionID(null);
            setUserName(null);

            SharePreKt.getSp().removeValuesForKeys(CommonConstant.Key.LOGIN_USER_ID, CommonConstant.Key.SESSION_ID, CommonConstant.Key.LOGIN_USER_NAME);

            Cxt.get().sendBroadcast(new Intent(CommonConstant.Action.LOGOUT())
                    .putExtra(CommonConstant.Action.LOGIN_LOGOUT_EXTRA_USER_ID, originalUserId)
            );
        }
    }

    public void loadPersistent() {
        int login_user_id = SharePreKt.getNonValue(CommonConstant.Key.LOGIN_USER_ID, -1);
        if (login_user_id > 0) {
            setUserID(login_user_id);
        }
        String session_id = SharePreKt.getValue(CommonConstant.Key.SESSION_ID, "");
        if (!TextUtils.isEmpty((session_id))) {
            setSessionID(session_id);
        }
        String login_user_name = SharePreKt.getValue(CommonConstant.Key.LOGIN_USER_NAME, "");
        if (!TextUtils.isEmpty(login_user_name)) {
            setUserName(login_user_name);
        }
    }


    public String getPhoneSN() {
        if (TextUtils.isEmpty(phoneSN)) {
            phoneSN = get_phone_sn();
        }
        return phoneSN;
    }

    public String getDeviceIMEI() {
        if (TextUtils.isEmpty(deviceIMEI)) {
            deviceIMEI = getPhoneSN();
        }
        return deviceIMEI;
    }

    public String getGetuiPushClientId() {
        return push_client_id == null ? "" : push_client_id;
    }

    public void setGetuiPushClientId(String pcid) {
        push_client_id = pcid;
    }

}
