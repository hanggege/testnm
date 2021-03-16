package com.mei.orc.john.network;

import com.mei.orc.john.model.ACCOUNT_get_checksum;
import com.mei.orc.john.model.AccountLoginResult;
import com.mei.orc.john.model.App_Version_Check_Info;
import com.mei.orc.john.model.CheckAccountResult;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.john.model.MOBILE_send_mobile_code;
import com.mei.orc.john.model.MOBILE_user_mobile_code;
import com.mei.orc.john.model.Oauth_Connect;
import com.mei.orc.john.model.Tim_get_tim_sig;
import com.mei.orc.john.model.UPLOAD_avatar;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by steven on 15/4/25.
 */
public interface JohnInterface {
    // 登录获取最新session_id， 调试提示： 如果想直接绕过先拿到session_id 请使用 'test_user_id'参数 ：
    // http://ruiz.nanrengou.test/account/login?test_user_id=1001  如果想测完整体流程，类似地址是：
    // http://ruiz.nanrengou.test/account/login?account_name=1000&phone_sn=XXXX&user_password=dd8c9e3dbf5a45b85a94af89ad438b84&checksum=11112124141127306790
    @GET("/account/login")
    Observable<AccountLoginResult.Response> ACCOUNT_login(
            @Query("account_name") String account_name, // 帐号名，现在支持手机号
            @Query("user_password")
                    String user_password, // 经过md5的密码, md5(check_sum + md5(check_sum前四位 + md5（default_salt +
            // 明文密码))),写死的default_salt为($*ngr^@%
            @Query("checksum") String checksum // 校验码
    );


    // 登录前先检测账号是否注册过
    @GET("/account/check_account_name")
    Observable<CheckAccountResult.Response> ACCOUNT_check_account_name(
            @Query("account_name") String account_name, // 帐号名，现在支持手机号
            @Query("phone_sn") String phone_sn // 用户手机机器码，用于标识设备
    );

    // 登录前先获取登录校验码
    // 调试提示：现在可以测试走通登录流程的帐号是1000,明文密码是：123456，详情请试图访问：http://ruiz.nanrengou.test/account/get_checksum?phone_sn=XXXX&account_name=1000&password_cleartext=123456
    @GET("/account/get_checksum")
    Observable<ACCOUNT_get_checksum.Response> ACCOUNT_get_checksum(
            @Query("account_name") String account_name // 帐号名，现在支持手机号
    );


    // 通过第三方登录后，直接注册，获得登录态及user_id
    @GET("/account/register_with_oauth")
    Observable<AccountLoginResult.Response> ACCOUNT_register_with_oauth(
            @Query("user_name") String user_name, // 用户昵称,必传
            @Query("access_token") String access_token, // 第三方登录成功后的access_token
            @Query("seq_id") Long seq_id // 访问/cooperate/connect_callback返回里面的seq_id，用于回传验证临时登录态
    );

    // 手机号,注册接口
    @GET("/account/register_with_phone_no")
    Observable<AccountLoginResult.Response> ACCOUNT_register_with_phone_no(
            @Query("user_name") String user_name, // 用户昵称
            @Query("user_salt") String user_salt, // 通过取验证码时，拿到的user_salt
            @Query("user_password")
                    String
                    user_password, // md5(user_salt + md5（default_salt + 明文密码）) ,其中default_salt 为写死的salt
            @Query("phone_no") String phone_no, // 帐号，第一版只支持手机号
            @Query("mobile_code") String mobile_code // 手机验证码
    );

    // 修改昵称
    @GET("/account/set_user_name")
    Observable<Empty_data.Response> ACCOUNT_set_user_name(@Query("user_name") String user_name // 新用户名
    );


    // 取消第三方登录的绑定,注意必须是已经有手机绑定否则报错
    //    @GET("/cooperate/unbind")
    @GET("/oauth/unbind")
    // 家超要求换的
    Observable<Empty_data.Response> COOPERATE_unbind(
            @Query("state") String state // 现支持：nanrengou_weibo_iphone,nanrengou_weibo_android
    );


    // 使用手机验证码来：修改密码/绑定手机号及密码/找回密码
    @GET("/mobile/use_mobile_code")
    Observable<MOBILE_user_mobile_code.Response> MOBILE_use_mobile_code(
            @Query("user_salt") String user_salt, // 通过取验证码时，拿到的user_salt
            @Query("user_password") String user_password, // md5(user_salt + md5（default_salt + 明文密码）) ,其中default_salt 为写死的salt
            @Query("phone_no") String phone_no, // 帐号，第一版只支持手机号
            @Query("mobile_code") String mobile_code, // 手机验证码
            @Query("mobile_code_next") String mobile_code_next // 获取手机号，下一步想做什么，现在支持：reset_password, logined_set_password,
            // logined_set_password_and_phone ,logined_set_phone
    );

    // 发送验证码
    @GET("/mobile/send_mobile_code")
    Observable<MOBILE_send_mobile_code.Response> MOBILE_send_mobile_code(
            @Query("phone_no") String phone_no, // 手机号【用于新绑定用户】
            @Query("mobile_code_next") String mobile_code_next // 获取手机号，下一步想做什么，现在支持：reset_password, register_with_phone_no,
            // logined_set_password, logined_set_password_and_phone
    );

    // 发送验证码
    @GET("/mobile/send_mobile_code_v2")
    Observable<MOBILE_send_mobile_code.Response> MOBILE_send_mobile_code2(
            @Query("phone_no") String phone_no, // 手机号【用于新绑定用户】
            @Query("mobile_code_next") String mobile_code_next, // 获取手机号，下一步想做什么，现在支持：reset_password, register_with_phone_no,
            // logined_set_password,
            // logined_set_password_and_phone,logined_set_phone
            @Query("captcha") String captcha, // 用户填写的图片验证码
            @Query("token") String token // 之前派发的token
    );

    //获取token和图片验证码
    @GET("/mobile/get_token")
    Observable<MOBILE_send_mobile_code.Response> MOBILE_get_token(@Query("phone_no") String phone_no);

    // 上传前，获取上传七牛凭证，及callback
    @GET("/upload/avatar")
    Observable<UPLOAD_avatar.Response> UPLOAD_avatar();

    // 验证手机验证码，验证码内部考虑了防刷机制	/mobile/verify_mobile_code
    @GET("/mobile/verify_mobile_code")
    Observable<Empty_data.Response> MOBILE_verify_mobile_code(
            @Query("phone_no") String phone_no, // 手机号
            @Query("mobile_code") String mobile_code // 手机验证码
            // 专用于注册过程短信验证码验证,所以不需要mobile_code_next参数
    );
  /*
      //上传后的回调方式
      @GET("/upload/avatar_callback")
      BaseResponse<?> UPLOAD_avatar_callback();
  */

    // 上报个推pcid
    @GET("/getui/update_cid")
    Observable<Empty_data.Response> Update_Cid();

    // 检测是否被踢
    @GET("/account/is_login")
    Observable<Empty_data.Response> Is_Login();


    // 客户端在登录腾讯云IM前，获取登录所需的userSig等信息	/tim/get_tim_sig
    @GET("/tim/get_tim_sig")
    Observable<Tim_get_tim_sig.Response> Tim_get_tim_sig(@Query("project_name") String project_name);


    // 使用authorization_code或openid + access_token方式进行第三方登录/绑定第三方帐号	/oauth/connect
    @GET("/oauth/connect")
    Observable<Oauth_Connect.Response> Oauth_Connect(
            @Query("state") String state,
            @Query("authorization_code") String authorization_code,
            @Query("openid") String openid,
            @Query("access_token") String access_token,
            @Query("bind") int bind,
            @Query("captcha") String captcha,
            @Query("token") String token);


    //客户端注册成功后上报到openinstall	/xiaolu/report_openinstall
    @POST("/xiaolu/report_openinstall")
    @FormUrlEncoded
    Observable<Empty_data.Response> Report_Openinstall(@Field("data") String data);

    @GET("/app_track/report")
    Observable<Empty_data.Response> douMengCpaReport(
            @Query("code") String code);


    @GET("/version/check")
    Observable<App_Version_Check_Info.Response> appVersionCheck();
}
