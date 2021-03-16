package com.net.model.radio;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * RadioFavoriteList
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-24
 */
public class RadioFavoriteListInfo {

    public List<RadioAudioInfo> list;

    public static class Response extends BaseResponse<RadioFavoriteListInfo> {

    }
}
