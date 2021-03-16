package com.net.model.chick.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.mei.orc.http.response.BaseResponse;
import com.mei.orc.john.model.AccountLoginResult;

/**
 * Created by steven on 15/4/27.
 */

public class LoginWithOauthResult extends AccountLoginResult implements Parcelable {

    public static class Response extends BaseResponse<LoginWithOauthResult> {

    }

    //"boolean,基本资料是否完善 false 代表未完善 需要走资料完善的三步骤 true 代表已完善"
    public boolean ifPerfect;
    public int isReg;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.ifPerfect ? (byte) 1 : (byte) 0);
        dest.writeInt(this.login_user_id);
        dest.writeInt(this.isReg);
        dest.writeString(this.easemob_app_salt);
        dest.writeString(this.login_user_name);
        dest.writeString(this.session_id);
        dest.writeString(this.token);
        dest.writeString(this.url);
    }

    public LoginWithOauthResult() {
    }

    protected LoginWithOauthResult(Parcel in) {
        this.ifPerfect = in.readByte() != 0;
        this.login_user_id = in.readInt();
        this.isReg = in.readInt();
        this.easemob_app_salt = in.readString();
        this.login_user_name = in.readString();
        this.session_id = in.readString();
        this.token = in.readString();
        this.url = in.readString();
    }

    public static final Creator<LoginWithOauthResult> CREATOR = new Creator<LoginWithOauthResult>() {
        @Override
        public LoginWithOauthResult createFromParcel(Parcel source) {
            return new LoginWithOauthResult(source);
        }

        @Override
        public LoginWithOauthResult[] newArray(int size) {
            return new LoginWithOauthResult[size];
        }
    };
}
