package com.mei.wood.ui.account;

import android.text.TextUtils;

import com.mei.orc.util.encrypt.DigestUtils;

/**
 * @author caowei
 * @email 646030315@qq.com
 * @time Created on 2016/10/20 0020
 */

public class CountryCodeUtil {

    private static final String DEFAULT_SALT = "($*ngr^@%";
    private static final String OPERATOR_PLUS = "+";
    private static final String OPERATOR_MINUS = "-";
    private static final String DEFAULT_COUNTRY_CODE = "86";

    public static String getOriginAccount(String account) {
        if (!TextUtils.isEmpty(account) && account.contains("-")) {
            return account.substring(account.indexOf("-") + 1);
        }
        return account;
    }

    public static String getOriginCountryCode(String account) {
        if (account.contains("-")) {
            return account.substring(0, account.indexOf("-"));
        }
        return "86";
    }

    /**
     * 加密注册或者use_mobile_code所需要的密码
     *
     * @param password 密码明文
     * @param salt     服务端返回
     */
    public static String calculateRegisterUseMobileCodePwd(String password, String salt) {
        return DigestUtils.md5Hex(salt + DigestUtils.md5Hex(DEFAULT_SALT + password));
    }

    /**
     * 加密登陆过程所需要的密码
     *
     * @param password 密码明文
     * @param checksum 服务端返回的校验码
     */
    public static String calculateLoginPwd(String password, String checksum) {
        return DigestUtils.md5Hex(checksum + DigestUtils.md5Hex(checksum.substring(0, 4) + DigestUtils.md5Hex(DEFAULT_SALT + password)));
    }

}
