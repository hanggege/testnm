package com.net.model.room;

import androidx.annotation.Nullable;

import com.mei.orc.http.response.BaseResponse;

/**
 * @author Created by lenna on 2020/7/1
 */
public class RoomExit {
    private boolean isExitRoom;
    private boolean firstShare;
    private String backendGround;
    private String testImage;
    private String shareUrl;
    private String backendGroundColor;
    private ShowTextBean showText;
    private String testTitle;

    @Nullable
    public String questionImage; //中间的背景图
    public int testNum; //第几道测试题

    public boolean isWechatCircle; //首次分享

    public ShowTextBean getShowText() {
        return showText;
    }

    public void setShowText(ShowTextBean showText) {
        this.showText = showText;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }


    public boolean isExitRoom() {
        return isExitRoom;
    }

    public void setExitRoom(boolean exitRoom) {
        isExitRoom = exitRoom;
    }

    public boolean isFirstShare() {
        return firstShare;
    }

    public void setFirstShare(boolean firstShare) {
        this.firstShare = firstShare;
    }

    public String getBackendGround() {
        return backendGround;
    }

    public void setBackendGround(String backendGround) {
        this.backendGround = backendGround;
    }

    public String getTestImage() {
        return testImage;
    }

    public void setTestImage(String testImage) {
        this.testImage = testImage;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getBackendGroundColor() {
        return backendGroundColor;
    }

    public void setBackendGroundColor(String backendGroundColor) {
        this.backendGroundColor = backendGroundColor;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public static class ShowTextBean {
        /**
         * color : #666666
         * fontScale : 10
         * text : 你有多受欢迎？
         */

        private String color;
        private int fontScale;
        private String text;

        private String leftImage;
        private String rightImage;

        public String getLeftImage() {
            return leftImage;
        }

        public void setLeftImage(String leftImage) {
            this.leftImage = leftImage;
        }

        public String getRightImage() {
            return rightImage;
        }

        public void setRightImage(String rightImage) {
            this.rightImage = rightImage;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int getFontScale() {
            return fontScale;
        }

        public void setFontScale(int fontScale) {
            this.fontScale = fontScale;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Response extends BaseResponse<RoomExit> {

    }

}
