package com.net.model.chick.evaluation;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.gift.GiftGuide;

import java.util.List;

/**
 * Created by zzw on 2019-12-15
 * Des:
 */
public class EvaluationAfterBean {
    public int grade;
    public String text;
    public int id;
    public List<GiftGuide.GiftGuideItem> gifts;

    public static class Response extends BaseResponse<EvaluationAfterBean> {

    }


}
