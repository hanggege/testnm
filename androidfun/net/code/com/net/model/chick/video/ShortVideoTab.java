package com.net.model.chick.video;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/9/1
 */
public class ShortVideoTab {
    public List<ShortVideoTab.TabInfo> tabs;
    @Nullable
    public String defaultTab;

    public static class Response extends BaseResponse<ShortVideoTab> {

    }

    public static class TabInfo {
        public String id;
        public String name;
    }
}
