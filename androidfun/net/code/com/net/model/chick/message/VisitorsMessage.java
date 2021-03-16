package com.net.model.chick.message;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.user.UserInfo;

import java.util.List;

/**
 * @author Created by lenna on 2020/6/15
 */
public class VisitorsMessage {

    public static class Response extends BaseResponse<VisitorsMessage> {

    }

    public VisitorsBean visitors;

    public static class VisitorsBean {
        public List<Visitors> list;
        public boolean hasMore = false;
        public int nextPageNo;
    }

    public static class Visitors {


        /**
         * roomChat : 0
         * theDay : 2020-06-12 00:00:00
         * updateTime : 2020-06-12 16:43:40
         * userId : 29911515
         * visitExclusiveChat : 1
         * visitHomepage : 1
         */


        private UserInfo info;
        private String theDay;
        private long updateTime;
        private int userId;
        private List<String> tags;
        private String titleTimeStr;
        private String timeStr;
        private boolean isVisibleTitle = false;
        private boolean isVisibleLine = true;

        public boolean isVisibleLine() {
            return isVisibleLine;
        }

        public void setVisibleLine(boolean visibleLine) {
            isVisibleLine = visibleLine;
        }

        public String getTitleTimeStr() {
            return titleTimeStr;
        }

        public void setTitleTimeStr(String titleTimeStr) {
            this.titleTimeStr = titleTimeStr;
        }

        public String getTimeStr() {
            return timeStr;
        }

        public void setTimeStr(String timeStr) {
            this.timeStr = timeStr;
        }

        public boolean isVisibleTitle() {
            return isVisibleTitle;
        }

        public void setVisibleTitle(boolean visibleTitle) {
            isVisibleTitle = visibleTitle;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public UserInfo getInfo() {
            return info;
        }

        public void setInfo(UserInfo info) {
            this.info = info;
        }


        public String getTheDay() {
            return theDay;
        }

        public void setTheDay(String theDay) {
            this.theDay = theDay;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}
