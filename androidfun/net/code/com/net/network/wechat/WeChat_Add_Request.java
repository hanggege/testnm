package com.net.network.wechat;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.wechat.WeChat_Number_Response;

import io.reactivex.Observable;

/**
 * Created by 杨强彪 on 2016/2/19.
 *
 * @描述：
 */
public class WeChat_Add_Request extends RxRequest<WeChat_Number_Response.Response, ApiInterface> {

    private int mentor_user_id;
    private String from_type;
    private String from_id;

    @Override
    public String toString() {
        return "WeChat_Add_Request{" +
                "mentor_user_id=" + mentor_user_id +
                ", from_type='" + from_type + '\'' +
                ", from_id='" + from_id + '\'' +
                '}';
    }

    public WeChat_Add_Request(int mentor_user_id, String from_type, String from_id) {
        super(WeChat_Number_Response.Response.class, ApiInterface.class);
        this.mentor_user_id = mentor_user_id;
        this.from_type = from_type;
        this.from_id = from_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<WeChat_Number_Response.Response> loadDataFromNetwork() throws Exception {
        return getService().get_WeChat_Number(mentor_user_id, from_type, from_id);
    }


}
