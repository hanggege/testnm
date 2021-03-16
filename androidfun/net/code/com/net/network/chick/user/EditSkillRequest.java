package com.net.network.chick.user;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * author : Song Jian
 * date   : 2020/2/20
 * desc   : 编辑擅长领域
 */

public class EditSkillRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    //擅长领域
    private String skills;
    //简介
    private String introduction;
    private String tags;

    public EditSkillRequest(String skills, String introduction, String tags) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.skills = skills;
        this.introduction = introduction;
        this.tags = tags;
    }


    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().editsTagsSkills(skills, introduction, tags);
    }

}
