package com.net.model.chat;


import com.joker.im.custom.chick.ServiceInfo;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.room.QueueMy;
import com.net.model.room.RoomInfo;

/**
 * Created by steven on 15/4/27.
 */

public class ExclusiveApply {

    public static class Response extends BaseResponse<ExclusiveApply> {

    }

    public RoomInfo roomInfo;
    public WaitInfo waitInfo;
    public String roomId;
    public String toast;//需要弹的toast信息
    public QueueMy.Option option;


    public static class WaitInfo {
        public String title;
        public String subTitle;
        public String avatar;
        public int gender;
        public Long timeout;
        public long couponId;
        public int categoryId;
        public String couponBackgroundImage;
        public String couponBackgroundName;
        public ServiceInfo specialService;
        public String timeoutToastForInitiator;
    }
}
