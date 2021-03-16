package com.net.model.chick.course;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */
public class CourseInfo {

    /**
     * 课程主键id
     */
    public int courseId;

    /**
     * 主播id
     */
    public int userId;

    /**
     * 课程名称
     */
    public String courseName;

    /**
     * 课程简介
     */
    public String courseIntroduction;

    /**
     * 课程亮点
     */
    public String courseHighlights;

    /**
     * 售价(钻)
     */
    public int cost;

    /**
     * 划线原价
     */
    public int crossedPrice;

    /**
     * 是否可以用
     */
    public boolean inUse;

    /**
     * 总讲数
     */
    public int audioCount;

    /**
     * 形象照
     */
    public String personalImage;
    public String backgroundColor;

    /**
     * 章节列表
     */
    public List<CourseChapter> courseChapterList;

    /**
     * 讲列表(没有章节的情况)
     */
    public List<AudioInfo> courseChapterAudioList;
}
