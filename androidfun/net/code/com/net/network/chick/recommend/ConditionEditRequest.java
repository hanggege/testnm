package com.net.network.chick.recommend;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/1/8
 * desc   : 更改征友条件
 */
public class ConditionEditRequest extends RxRequest<Empty_data.Response, ApiInterface> {

    public String ageBegin;
    public String ageEnd;
    public String conditionId;
    public String heightBegin;
    public String heightEnd;
    public String location;
    public String lowestEducation;
    public String lowestIncome;


    public ConditionEditRequest() {
        super(Empty_data.Response.class, ApiInterface.class);
    }

    @Override
    public String toString() {
        return "ConditionEditRequest{" +
                "ageBegin='" + ageBegin + '\'' +
                ", ageEnd='" + ageEnd + '\'' +
                ", conditionId='" + conditionId + '\'' +
                ", heightBegin='" + heightBegin + '\'' +
                ", heightEnd='" + heightEnd + '\'' +
                ", location='" + location + '\'' +
                ", lowestEducation='" + lowestEducation + '\'' +
                ", lowestIncome='" + lowestIncome + '\'' +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().editPersonalsCondition(location, ageBegin, ageEnd, heightBegin, heightEnd, lowestIncome, lowestEducation);
    }


}
