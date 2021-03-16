package com.net.network.report;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/3/12
 */
public class GetuiReportRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String refer_id;
    public String refer_type;
    public String getui_push_log_id;

    public GetuiReportRequest(String refer_id, String refer_type, String getui_push_log_id) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.refer_type = refer_type;
        this.refer_id = refer_id;
        this.getui_push_log_id = getui_push_log_id;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().GetuiReport(refer_id, refer_type, getui_push_log_id);
    }
}
