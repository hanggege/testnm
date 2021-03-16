package com.net.model.room;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2020-03-11.
 */
public class UpstreamTypeList {

    public QueueMy.Option option;
    public List<UpstreamTypeItem> list;
    public boolean isApplyOtherRoom;

    public static class Response extends BaseResponse<UpstreamTypeList> {
    }


    public static class UpstreamTypeItem {
        public String serviceOrderId;
        public String title;
        public String subTitle;
        public List<SplitText> text;
        public int linkType;
    }
}
