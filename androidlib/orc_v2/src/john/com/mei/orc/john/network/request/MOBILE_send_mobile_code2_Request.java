package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.MOBILE_send_mobile_code;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class MOBILE_send_mobile_code2_Request extends RxRequest<MOBILE_send_mobile_code.Response, JohnInterface> {

    public static final String NEXT_reset_password = "reset_password";
    public static final String NEXT_register_with_phone_no = "register_with_phone_no";
    public static final String NEXT_logined_set_password = "logined_set_password";
    public static final String NEXT_logined_set_password_and_phone = "logined_set_password_and_phone";
    public static final String logined_or_register_with_phone_no = "logined_or_register_with_phone_no";

    private String phone_no = "";
    private String mobile_code_next = "";
    private String captcha = "";
    private String token = "";

    @Override
    public String toString() {
        return "MOBILE_send_mobile_code2_Request{" +
                "phone_no='" + phone_no + '\'' +
                ", mobile_code_next='" + mobile_code_next + '\'' +
                '}';
    }

    /**
     * @param phone_no
     * @param mobile_code_next MOBILE_send_mobile_code2_Request.NEXT_XXX
     */
    public MOBILE_send_mobile_code2_Request(String phone_no, String mobile_code_next) {
        this(phone_no, mobile_code_next, "", "");
    }

    public MOBILE_send_mobile_code2_Request(String phone_no, String mobile_code_next, String captcha, String token) {
        super(MOBILE_send_mobile_code.Response.class, JohnInterface.class);
        this.phone_no = phone_no;
        this.mobile_code_next = mobile_code_next;
        this.captcha = captcha;
        this.token = token;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<MOBILE_send_mobile_code.Response> loadDataFromNetwork() throws Exception {
        return getService().MOBILE_send_mobile_code2(phone_no, mobile_code_next, captcha, token);
    }
}
