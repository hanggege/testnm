package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * TestReportDialogRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-24
 */
public class TestReportDialogRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public int login_user_id;
    public String session_id;

    public TestReportDialogRequest(int login_user_id, String session_id) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.login_user_id = login_user_id;
        this.session_id = session_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().reportTestDialog(login_user_id > 0 ? String.valueOf(login_user_id) : "", session_id);
    }
}
