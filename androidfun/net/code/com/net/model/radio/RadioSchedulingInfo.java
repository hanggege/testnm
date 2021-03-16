package com.net.model.radio;

import java.util.List;

/**
 * RadioTimingListInfo
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-24
 */
public class RadioSchedulingInfo {
    public String defaultTab;
    public List<RadioScheduleBean> list;

    public static class RadioScheduleBean {

        public String id;
        public int stopAfterPlayAudios;
        public int stopAfterPlaySeconds;
        public String title;
    }
}
