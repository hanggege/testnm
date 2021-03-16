package com.mei.orc.john.model;


import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-06
 */
public class App_Version_Check_Info {


    public static class Response extends BaseResponse<App_Version_Check_Info> {

    }

    public String path;
    public String update_version;
    public String update_message;
    public int delay_remind_day;
    public boolean force;


}
