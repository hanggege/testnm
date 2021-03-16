package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.room.QueueShow;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class QueueShowRequest extends RxRequest<QueueShow.Response, ApiInterface> {

    private String roomId;
    private int type;//1代表连线申请列表 2代表查询围观用户 3代表查询在线用
    private int pageNo;
    private int pageSize;

    public QueueShowRequest(String roomId, int type, int pageNo, int pageSize) {
        super(QueueShow.Response.class, ApiInterface.class);
        this.roomId = roomId;
        this.type = type;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<QueueShow.Response> loadDataFromNetwork() throws Exception {
        return getService().QueueShow(roomId, type, pageNo, pageSize);
    }
}
