package com.net.network.report;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * Created by Joker on 2015/7/17.
 */
public class REPORT_add_Request extends RxRequest<Empty_data.Response, ApiInterface> {
    public static final String COMMENT_TYPE = "comment";
    private long feed_id;
    private int user_id;
    private int report_type;
    private int login_user_id;
    private String remark;
    private String feed_content_type;


    public REPORT_add_Request(long feedID, int user_id, int reportType, int userID, String remark, boolean isComment) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.feed_id = feedID;
        this.user_id = user_id;
        this.report_type = reportType;
        this.login_user_id = userID;
        this.remark = remark;
        this.feed_content_type = isComment ? COMMENT_TYPE : "";
    }


    @Override
    public String toString() {
        return "REPORT_add_Request{" +
                "feed_id=" + feed_id +
                ", report_type=" + report_type +
                ", login_user_id=" + login_user_id +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().REPORT_add(feed_id, user_id, report_type, login_user_id, feed_content_type, remark);
    }
}
