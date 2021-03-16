package com.net.model.chick.course;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */
public class CourseChapter {
    /**
     * 课程章节主键id
     */
    public int course_chapters_id;

    /**
     * 课程主键id
     */
    public int course_id;

    /**
     * 章节标题
     */
    public String chapters_title;

    /**
     * 章节介绍
     */
    public String chapters_introduction;

    /**
     * 章节时长
     */
    public int chapters_duration;

    /**
     * 课程讲列表
     */
    public List<AudioInfo> courseChapterAudioList;
}
