package com.net.model.room;

import com.joker.im.custom.chick.SplitText;

import java.util.List;

/**
 * CouponLabelInfo
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-11
 */
public class CouponLabelInfo {
    public SplitText applicationExplain;
    public SplitText applicationScopeText;
    public int couponId;
    public SplitText couponName;
    public long couponNum;
    public SplitText couponTags;
    public List<SplitText> discountName;
    public boolean disable;
    public double discount;
    public SplitText timeLimit;
    public boolean todayEnd;
    public String action;
}
