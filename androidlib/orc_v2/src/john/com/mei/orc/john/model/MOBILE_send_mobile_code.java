package com.mei.orc.john.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by steven on 15/4/27.
 */

public class MOBILE_send_mobile_code implements Parcelable {

    public static class Response extends BaseResponse<MOBILE_send_mobile_code> {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone_no);
        dest.writeString(this.user_salt);
        dest.writeString(this.token);
        dest.writeString(this.url);
    }

    public MOBILE_send_mobile_code() {
    }

    protected MOBILE_send_mobile_code(Parcel in) {
        this.phone_no = in.readString();
        this.user_salt = in.readString();
        this.token = in.readString();
        this.url = in.readString();
    }

    public String phone_no;
    public String user_salt = "";
    public String token;
    public String url;

    public static final Creator<MOBILE_send_mobile_code> CREATOR = new Creator<MOBILE_send_mobile_code>() {
        @Override
        public MOBILE_send_mobile_code createFromParcel(Parcel source) {
            return new MOBILE_send_mobile_code(source);
        }

        @Override
        public MOBILE_send_mobile_code[] newArray(int size) {
            return new MOBILE_send_mobile_code[size];
        }
    };
}
