package com.net.network.chick.workroom;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.workroom.WorkRoomServiceLivingRoomInfo;

import io.reactivex.Observable;

/**
 * WorkRoomServiceLivingRoomInfoRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-12
 */
public class WorkRoomServiceLivingRoomInfoRequest extends RxRequest<WorkRoomServiceLivingRoomInfo.Response, ApiInterface> {

    private int publisherId;

    public WorkRoomServiceLivingRoomInfoRequest(int publisherId) {
        super(WorkRoomServiceLivingRoomInfo.Response.class, ApiInterface.class);
        this.publisherId = publisherId;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<WorkRoomServiceLivingRoomInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getSpecialServiceRoomInfo(publisherId);
    }
}
