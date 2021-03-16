package com.net.network.chick.login;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.JohnUser;
import com.mei.orc.john.model.MOBILE_send_mobile_code;
import com.net.ApiInterface;
import com.net.MeiUser;
import com.net.model.user.UserInfo;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class SendMobileCodeRequest extends RxRequest<MOBILE_send_mobile_code.Response, ApiInterface> {


    private String phoneNo = "";
    private String captcha = "";


    public SendMobileCodeRequest(String phoneNo, String captcha) {
        super(MOBILE_send_mobile_code.Response.class, ApiInterface.class);
        this.phoneNo = phoneNo;
        this.captcha = captcha;
    }

    @Override
    public String toString() {
        return "SendMobileCodeRequest{" +
                "phoneNo='" + phoneNo + '\'' +
                ", captcha='" + captcha + '\'' +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<MOBILE_send_mobile_code.Response> loadDataFromNetwork() throws Exception {

        String nextCode = "";
        if (JohnUser.getSharedUser().hasLogin()) {
            UserInfo userInfo = MeiUser.getSharedUser().info;
            if (userInfo != null) {
                if (userInfo.phoneAuthStatus == 0) {
                    nextCode = "logined_set_phone";
                } else {
                    nextCode = "logined_reset_phone";
                }
            }
        }
        return getService().sendMobileCode(phoneNo, captcha, nextCode);
    }
}
