package com.net.model.chick.evaluation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzw on 2019-12-15
 * Des:
 */
public class EvaluationBean {


    /**
     * roomId : sting相亲房间Id不能为空
     * type : int 评价类型 0评价连线用户 1 评价红娘
     * ratedUser : int 被评价的用户
     * grade : int 用户满意度 评价红娘时使用 1-满意 2-一般 3-不满意
     * content : sting评价内容/理由
     * tagId : int 评价红娘时使用 评价标签id
     */

    public String roomId;
    public int type;
    public int ratedUser;
    public int grade;
    public String content;
    public List<Integer> tags = new ArrayList<>();


}
