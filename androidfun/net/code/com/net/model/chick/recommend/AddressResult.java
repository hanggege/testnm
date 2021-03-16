package com.net.model.chick.recommend;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by zzw on 2019-12-20
 * Des:
 */
public class AddressResult {
    public AddressResult(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static class Response extends BaseResponse<List<AddressResult>> {

    }


    /**
     * id : string,省/市/县ID
     * name : string,省/市/县名称
     */
    public String id;
    public String name;
    public boolean isCheck = false;

}
