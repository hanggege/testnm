package com.net.model.room;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.pay.ProductBean;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-15
 */
public class RoomStatus {

    public static class Response extends BaseResponse<RoomStatus> {

    }

    public long stay;
    public int diff;
    public long begin;
    public int balance;
    public boolean showRecharge;
    public int usedFreeChanceNow;
    public int maxFreeDuration;
    public SplitText timePrefix;//时间前缀
    public SplitText balanceTips;//余额提醒
    public Alert alert;
    public int duration;
    public int totalAvailableDuration; //免费连线总时长
    public String countdownColor;
    public int remainDuration;
    public int couponRemainDuration;
    public int showRechargeDialogTime;
    public boolean groupRoomIsOpened;
    public boolean showRemainDuration;

    public static class Alert {
        public boolean canClose; //能否关闭
        public List<ProductBean> products;
        public int timeout;
        public SplitText subTitle;
        public SplitText title;
        public int defaultSelect;
    }

    public static class ProductItem {
        public int cost;
        public String costText;
        public int minutes;
        public String minutesText;
        public int productId;
        public int coinNum;
    }

}
