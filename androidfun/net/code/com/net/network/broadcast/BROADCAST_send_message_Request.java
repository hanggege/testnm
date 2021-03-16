package com.net.network.broadcast;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by Joker on 2015/8/20.
 */
public class BROADCAST_send_message_Request extends RxRequest<Empty_data.Response, ApiInterface> {

    public static enum BroadCast_Type {
        send_text,//发言内容:
        send_question, //提问内容:
        send_photo, // 图片Url:
        begin_upstream, // 连线：
        end_upstream  //下麦:
    }

    private String room_id;
    private int broadcast_id;
    private String type;
    private String content;
    private String photo_id;

    public BROADCAST_send_message_Request(int broadcast_id, String content, String photo_id, BroadCast_Type type) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.broadcast_id = broadcast_id;
        this.room_id = "";
        this.type = type.name();
        this.content = content;
        this.photo_id = photo_id;
    }

    public BROADCAST_send_message_Request(int broadcast_id, String room_id, String content, String photo_id, BroadCast_Type type) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.broadcast_id = broadcast_id;
        this.room_id = room_id;
        this.type = type.name();
        this.content = content;
        this.photo_id = photo_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().BROADCAST_send_message(broadcast_id, room_id, type, content, photo_id);
    }
}
