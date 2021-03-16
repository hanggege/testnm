package com.mei.push;

import java.util.HashMap;
import java.util.List;

/**
 * Created by steven on 3/18/16.
 */
public class Payload {

    public String title = "";
    public String content = "";
    public String getui_push_log_id = "";
    /**
     * 用户点击直接触达
     **/
    public ActionInfo cmd;

    /**
     * 弹框式推送
     **/
    public ActionInfo leftAction;
    public ActionInfo rightAction;

    public static class ActionInfo {
        public String text = "";
        public String action = "";
        public String actionId = "";
        public String actionType = "";

        /**
         * 给gio打点上报
         **/
        public List<GioReport> gioReportList;
    }


    public static class GioReport {
        public String gioEventKey;
        public HashMap<String, String> gioValue = new HashMap();
    }
}
