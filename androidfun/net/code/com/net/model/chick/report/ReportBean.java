package com.net.model.chick.report;

import java.util.List;

/**
 * Created by zzw on 2019-12-14
 * Des:
 */
public class ReportBean {


    /**
     * reportUser : int 被举报的用户
     * roomId : sting房间id
     * reason : sting举报理由
     * description : sting举报描述
     * userPhone : sting举报用户手机号
     * images : ["sting图片路径"]
     */

    public int reportUser;
    public String roomId;
    public int reasonId;
    public String description;
    public String userPhone;
    public List<String> images;


}
