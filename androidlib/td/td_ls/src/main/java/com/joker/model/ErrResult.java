package com.joker.model;


import com.joker.flag.TdFlags;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public class ErrResult {
    @TdFlags
    public int type;
    public int code;
    public String msg;

    /**
     * @param code {@link com.joker.constant.TdErrorCode} 错误码（可通过错误码自定义错误提示，也可用msg）
     */
    public ErrResult(@TdFlags int type, int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }
}
