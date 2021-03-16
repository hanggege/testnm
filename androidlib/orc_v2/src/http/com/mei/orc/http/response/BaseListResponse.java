package com.mei.orc.http.response;

import java.util.List;

/**
 * Created by Joker on 2015/12/18.用于data是列表的接口
 */

public class BaseListResponse<T> extends DataResponse {


    private List<T> data;

    @Override
    public String toString() {
        return "BaseListResponse{" +
                "data=" + data +
                '}';
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }


}
