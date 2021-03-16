package com.mei.faceunity.entity;

public class TestBean {

    /**
     * rtn : 0
     * msg : 请求成功
     * data : {"custom":{"camera":"{\"skinTypeList\":[{\"skinMap\":{\"whitening\":0.6,\"precisebeauty\":1.0},\"type\":\"CACHE_FACE_SKIN_DEFAULT_PARAMS\"},{\"skinMap\":{\"facev\":0.84,\"narrowface\":0.71},\"type\":\"CACHE_FACE_SHAPE_DEFAULT_PARAMS\"},{\"skinMap\":{\"FilterLevel_fennen3\":1.0,\"FilterLevel_xiaoqingxin1\":0.8,\"FilterLevel_lengsediao1\":1.0,\"FilterLevel_xiaoqingxin6\":0.34,\"FilterLevel_fennen1\":0.7},\"type\":\"CACHE_FilterLevel\"},{\"skinMap\":{\"清透\":0.43},\"type\":\"CACHE_BatchMakeupLevel\"}]}"}}
     */

    private int rtn;
    private String msg;
    private DataBean data;

    public int getRtn() {
        return rtn;
    }

    public void setRtn(int rtn) {
        this.rtn = rtn;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * custom : {"camera":"{\"skinTypeList\":[{\"skinMap\":{\"whitening\":0.6,\"precisebeauty\":1.0},\"type\":\"CACHE_FACE_SKIN_DEFAULT_PARAMS\"},{\"skinMap\":{\"facev\":0.84,\"narrowface\":0.71},\"type\":\"CACHE_FACE_SHAPE_DEFAULT_PARAMS\"},{\"skinMap\":{\"FilterLevel_fennen3\":1.0,\"FilterLevel_xiaoqingxin1\":0.8,\"FilterLevel_lengsediao1\":1.0,\"FilterLevel_xiaoqingxin6\":0.34,\"FilterLevel_fennen1\":0.7},\"type\":\"CACHE_FilterLevel\"},{\"skinMap\":{\"清透\":0.43},\"type\":\"CACHE_BatchMakeupLevel\"}]}"}
         */

        private CustomBean custom;

        public CustomBean getCustom() {
            return custom;
        }

        public void setCustom(CustomBean custom) {
            this.custom = custom;
        }

        public static class CustomBean {
            /**
             * camera : {"skinTypeList":[{"skinMap":{"whitening":0.6,"precisebeauty":1.0},"type":"CACHE_FACE_SKIN_DEFAULT_PARAMS"},{"skinMap":{"facev":0.84,"narrowface":0.71},"type":"CACHE_FACE_SHAPE_DEFAULT_PARAMS"},{"skinMap":{"FilterLevel_fennen3":1.0,"FilterLevel_xiaoqingxin1":0.8,"FilterLevel_lengsediao1":1.0,"FilterLevel_xiaoqingxin6":0.34,"FilterLevel_fennen1":0.7},"type":"CACHE_FilterLevel"},{"skinMap":{"清透":0.43},"type":"CACHE_BatchMakeupLevel"}]}
             */

            private String camera;

            public String getCamera() {
                return camera;
            }

            public void setCamera(String camera) {
                this.camera = camera;
            }
        }
    }
}
