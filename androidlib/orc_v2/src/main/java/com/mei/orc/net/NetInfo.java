package com.mei.orc.net;

/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/1/14.
 */

public class NetInfo {
    public NetType preType;
    public NetType currTye;

    public NetInfo(NetType preType, NetType currTye) {
        this.preType = preType;
        this.currTye = currTye;
    }


    public enum NetType {
        NONE,
        WIFI,
        MOBILE;

        public static NetType parseType(final String type) {
            NetType netType = NONE;
            for (NetType net : values()) {
                if (net.name().equals(type)) {
                    netType = net;
                }
            }
            return netType;
        }
    }
}
