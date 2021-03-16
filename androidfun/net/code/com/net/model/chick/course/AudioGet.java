package com.net.model.chick.course;

import com.joker.im.custom.chick.PublisherInfo;
import com.mei.orc.http.response.BaseResponse;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */
public class AudioGet {
    public PublisherInfo publisherInfo;
    public CourseInfo course;
    public int index;
    public AudioInfo audio;

    public static class Response extends BaseResponse<AudioGet> {

    }
}

