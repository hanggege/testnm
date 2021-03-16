package com.net.model.chick.course;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */
public class AudioInfo {
    /**
     * 课程章节音频主键id
     */
    public int id;

    /**
     * 对应course_chapters表course_chapters_id,-1表示不属于章
     */
    public int courseChaptersId;

    /**
     * 对应course的course_id
     */
    public int courseId;

    /**
     * 音频标题
     */
    public String audioTitle;

    /**
     * 音频资源路径
     */
    public String audioUrl;

    /**
     * 音频文稿
     */
    public String audioDraft;

    /**
     * 是否开启文稿
     */
    public Byte isOpenDraft;

    /**
     * 是否免费试听
     */
    public Byte isFreeAudition;
    //时长
    public int audioDuration;
    // 开始进度
    public int progress;

    /**
     * 获取音频开始位置
     */
    public int getStartPosition() {
        if (progress < 99) {
            return audioDuration * progress / 100;
        } else {
            return 0;
        }
    }

}
