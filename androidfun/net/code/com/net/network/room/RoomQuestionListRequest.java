package com.net.network.room;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.room.RoomQuestionsListResult;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */
public class RoomQuestionListRequest extends RxRequest<RoomQuestionsListResult.Response, ApiInterface> {

    public RoomQuestionListRequest() {
        super(RoomQuestionsListResult.Response.class, ApiInterface.class);
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RoomQuestionsListResult.Response> loadDataFromNetwork() throws Exception {
        return getService().roomQuestionList();
    }
}
