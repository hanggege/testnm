package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by hang on 2020-03-11.
 */
public class UpstreamTypeText {

    public String text;
    public int type;
    public boolean hasApply;

    public static class Response extends BaseResponse<UpstreamTypeText> {
    }


}
