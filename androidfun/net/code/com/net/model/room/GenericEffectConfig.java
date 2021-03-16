package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/16
 */
public class GenericEffectConfig {

    public List<EffectConfigItem> list;
    public String rechargeSuccessAnimation;
    public String redPacket;
    public ShortVideoEffect shortVideoEffect;

    public static class Response extends BaseResponse<GenericEffectConfig> {

    }

    public static class EffectConfigItem {
        public int cost;
        public String effect;
        public int level;
        public String name;
        public String text;
    }
}
