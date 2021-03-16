package com.net.model.chick.update;

import java.util.List;

/**
 * Created by zzw on 2019-12-27
 * Des:
 */
public class CheckViolationBean {


    /**
     * data : {"uri":"https://mars-assets.qnssl.com/resource/gogopher.jpg"}
     * params : {"scenes":["pulp","terror","politician"]}
     */

    public DataBean data;
    public ParamsBean params;

    public static class DataBean {
        public DataBean(String uri) {
            this.uri = uri;
        }

        public String uri;
    }

    public static class ParamsBean {
        public ParamsBean(List<String> scenes) {
            this.scenes = scenes;
        }

        public List<String> scenes;
    }
}
