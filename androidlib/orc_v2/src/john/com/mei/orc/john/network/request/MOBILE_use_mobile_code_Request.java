package com.mei.orc.john.network.request;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.MOBILE_user_mobile_code;
import com.mei.orc.john.network.JohnInterface;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class MOBILE_use_mobile_code_Request extends RxRequest<MOBILE_user_mobile_code.Response, JohnInterface> {

    public static final String NEXT_reset_password = "reset_password";
    public static final String NEXT_logined_set_password = "logined_set_password";
    public static final String NEXT_logined_set_password_and_phone = "logined_set_password_and_phone";
    public static final String logined_or_register_with_phone_no = "logined_or_register_with_phone_no";


    private String user_salt = "";
    private String user_password = "";
    private String phone_no = "";
    private String mobile_code = "";
    private String mobile_code_next = "";

    @Override
    public String toString() {
        return "MOBILE_use_mobile_code_Request{" +
                "user_salt='" + user_salt + '\'' +
                ", user_password='" + user_password + '\'' +
                ", phone_no='" + phone_no + '\'' +
                ", mobile_code='" + mobile_code + '\'' +
                ", mobile_code_next='" + mobile_code_next + '\'' +
                '}';
    }

    /**
     * @param user_salt
     * @param user_password
     * @param phone_no
     * @param mobile_code
     * @param mobile_code_next MOBILE_use_mobile_code_Request.NEXT_XXX
     */
    public MOBILE_use_mobile_code_Request(String user_salt, String user_password, String phone_no, String mobile_code, String mobile_code_next) {
        super(MOBILE_user_mobile_code.Response.class, JohnInterface.class);
        this.user_salt = user_salt;
        this.user_password = user_password;
        this.phone_no = phone_no;
        this.mobile_code = mobile_code;
        this.mobile_code_next = mobile_code_next;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<MOBILE_user_mobile_code.Response> loadDataFromNetwork() throws Exception {
        return getService().MOBILE_use_mobile_code(user_salt, user_password, phone_no, mobile_code, mobile_code_next);
    }

}
