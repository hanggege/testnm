package com.net.model.gift;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by hang on 2019-12-17.
 */
public class GiftGuide {

    public static class Response extends BaseResponse<GiftGuide> {
    }

    public List<GiftGuideItem> gifts;
    public String baseInfo;

    public static class GiftGuideItem {
        public int giftId;
        public String giftImage;
        public String giftName;
        public int num;
        public int giftType;
        public String text;
        public int costCoin;
    }
}
