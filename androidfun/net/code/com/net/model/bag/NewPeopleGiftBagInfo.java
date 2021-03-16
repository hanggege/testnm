package com.net.model.bag;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/11/17
 * 新人礼包实体
 */
public class NewPeopleGiftBagInfo {

    @Nullable
    public String button;
    @Nullable
    public String line;
    @Nullable
    public String background;
    @Nullable
    public GiftBag bag;
    @Nullable
    public String buyToast;

    public static class Response extends BaseResponse<NewPeopleGiftBagInfo> {

    }

    public class GiftBag {
        public int seqId;
        @Nullable
        public String bagName;
        public double price;
        @Nullable
        public List<SplitText> buyButtonText;
        public double originalPrice;
        @Nullable
        public List<GiftBagResources> resources;
        @Nullable
        public String errorMsg;
    }

    public class GiftBagResources {
        public int resourceType;
        public int resourceId;
        @Nullable
        public List<SplitText> tag;
        @Nullable
        public List<SplitText> mainTitle;
        @Nullable
        public String mainTitleText;
        @Nullable
        public List<SplitText> subTitle;
        @Nullable
        public String backGround;
    }

}
