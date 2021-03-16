package com.mei.orc.http.response;

import android.text.TextUtils;

/**
 * Created by joker on 15/12/18.
 */
public class DataResponse {

    public int rtn;

    public String msg;

    public String trace = "";

    public int getRtn() {
        return rtn;
    }

    public String getErrMsg() {
        if (TextUtils.isEmpty(msg)) {
            return "";
        } else {
            return msg;
        }
    }

    public boolean isSuccess() {
        return rtn == 0;
    }
}
