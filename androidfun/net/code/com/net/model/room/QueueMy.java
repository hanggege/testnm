package com.net.model.room;

import com.joker.im.custom.chick.Extra;
import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.pay.ProductBean;

import java.util.List;

/**
 * Created by hang on 2020-03-11.
 */
public class QueueMy {

    public boolean isApply;
    public boolean isApplyOtherRoom;
    public Option option;

    public static class Response extends BaseResponse<QueueMy> {
    }

    public static class Option {
        public int balance;
        public boolean needShowRecharge;
        public boolean defaultNotShowFaceSelected;
        public int defaultSelect;
        public String notShowFaceTips;
        public List<ProductBean> products;
        public SplitText subTitle;
        public List<SplitText> tips;
        public SplitText title;
        public String userBalanceText;
        public boolean supportAudioChat;
        public boolean isFree;
        public String applyBtnText;
        public String protectIcon;
        public Extra extra;
    }

}
