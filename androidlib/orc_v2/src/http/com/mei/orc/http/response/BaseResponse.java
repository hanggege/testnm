package com.mei.orc.http.response;

public class BaseResponse<T> extends DataResponse {


    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
