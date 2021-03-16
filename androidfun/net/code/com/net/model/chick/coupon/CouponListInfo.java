package com.net.model.chick.coupon;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * CouponListInfo
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-11
 */
public class CouponListInfo {
    public static class Response extends BaseResponse<CouponListInfo> {

    }

    public SplitText limitTimeText;
    public SplitText discountText;
    public List<CouponListLabelInfo> targets;

    public static class CouponListLabelInfo {
        /**
         * action : dove://new_json_webview/{"appear_top_bar":0,"hide_nav_bottomline":0,"forbid_navi_swip":1,"can_bounce":0,"can_refresh":0,"go_back_android":1,"url":"http://zxl.meidongli.net/zhixin/exclusive-service-info2.html?service_id_id=7"}
         * button : {"color":"#FFFFFF","text":"点击使用"}
         * cateTag : {"color":"#FF3A3A","text":"课程"}
         * discountPriceText : [{"color":"#FF3A3A","fontScale":1,"text":"券后:157","verticalCenter":false},{"fontScale":0.7,"text":"💎","verticalCenter":true}]
         * mainTitle : {"color":"#333333","text":"测试"}
         * priceText : [{"color":"#999999","fontScale":1,"text":"175","verticalCenter":false},{"fontScale":0.7,"text":"💎","verticalCenter":true}]
         */

        public int resourceId;
        public int resourceType;
        public String action;
        public SplitText button;
        public SplitText cateTag;
        public SplitText mainTitle;
        public List<SplitText> discountPriceText;
        public List<SplitText> priceText;
    }
}
