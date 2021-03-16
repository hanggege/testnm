package com.net.model.broadcast;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/10/30
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

/**
 * 播放来源
 */
@StringDef(value = {
        PlayType.NONE,
        PlayType.LISTENING_LIVE,
        PlayType.SPEAKING_LIVE,
        PlayType.LISTENING_SERVICE,
        PlayType.LISTENING_RADIO})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayType {

    String NONE = "";
    String LISTENING_LIVE = "LISTENING_LIVE";
    String SPEAKING_LIVE = "SPEAKING_LIVE";
    String LISTENING_SERVICE = "LISTENING_SERVICE";
    String LISTENING_RADIO = "LISTENING_RADIO";


}
