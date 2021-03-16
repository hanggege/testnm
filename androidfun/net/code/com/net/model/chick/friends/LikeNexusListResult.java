package com.net.model.chick.friends;


import com.mei.orc.http.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzw on 2020-01-11
 * Des:
 */
public class LikeNexusListResult {

    public List<String> myLikeList = new ArrayList<>();
    public List<String> likeMyList = new ArrayList<>();
    public List<String> blackList = new ArrayList<>();

    public static class Response extends BaseResponse<LikeNexusListResult> {

    }
}
