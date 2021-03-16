package com.net.network.wechat;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by Ling on 2018/2/27.
 *
 * @描述：
 */
public class WeChat_Copy_Request extends RxRequest<Empty_data.Response, ApiInterface> {

    private int mentor_user_id;
    private int send_im;
    //表示是否发送im消息 消息内容为进入微学堂小程序的内链
    private boolean send_im_mp;

    @Override
    public String toString() {
        return "WeChat_Copy_Request{" +
                "mentor_user_id=" + mentor_user_id +
                "send_im=" + send_im +
                '}';
    }

    public WeChat_Copy_Request(int mentor_user_id) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.mentor_user_id = mentor_user_id;
        this.send_im = 1;
    }

    public WeChat_Copy_Request(int mentor_user_id, boolean send_im_mp) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.mentor_user_id = mentor_user_id;
        this.send_im_mp = send_im_mp;
        this.send_im = 1;
    }

    public WeChat_Copy_Request(int mentor_user_id, int send_im) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.mentor_user_id = mentor_user_id;
        this.send_im = send_im;
    }

    public WeChat_Copy_Request(int mentor_user_id, int send_im, boolean send_im_mp) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.mentor_user_id = mentor_user_id;
        this.send_im = send_im;
        this.send_im_mp = send_im_mp;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().weChat_Had_Copy(mentor_user_id, send_im, send_im_mp);
    }


}
