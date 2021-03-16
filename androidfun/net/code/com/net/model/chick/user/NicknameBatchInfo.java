package com.net.model.chick.user;

import androidx.annotation.Nullable;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by zzw on 2019-12-09
 * Des:
 */
public class NicknameBatchInfo {

    @Nullable
    public String nicknameWx; //微信名称
    @Nullable
    public String avatar;
    public List<String> list;

    public static class Response extends BaseResponse<NicknameBatchInfo> {

    }

}
