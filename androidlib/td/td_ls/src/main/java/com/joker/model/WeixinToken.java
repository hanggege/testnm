package com.joker.model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/28.
 */

public class WeixinToken {

    public String access_token;
    public int expires_in;
    public String refresh_token;
    public String openid;
    public String scope;
    public String unionid;


    public static WeixinToken parseJsonObject(String result) {
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        try {
            WeixinToken token = new WeixinToken();
            JSONObject json = new JSONObject(result);
            token.access_token = getValue(json, "access_token");
            token.expires_in = getInt(json, "expires_in");
            token.refresh_token = getValue(json, "refresh_token");
            token.openid = getValue(json, "openid");
            token.scope = getValue(json, "scope");
            token.unionid = getValue(json, "unionid");
            return token;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getValue(JSONObject json, String key) {
        String value = "";
        try {
            value = json.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private static int getInt(JSONObject json, String key) {
        int value = 0;
        try {
            value = json.getInt(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
