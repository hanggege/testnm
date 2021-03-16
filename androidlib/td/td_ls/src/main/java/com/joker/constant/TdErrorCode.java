package com.joker.constant;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public interface TdErrorCode {
    int NONE_CODE_ERR = -100;
    int USER_CANCEL_ERR = -110;//用户主动取消
    int DATA_IS_ERR = -112;//数据出错
    int IMAGE_DECODE_ERR = -119;//图片解析出错
    //WEIXIN
    int NOT_INSTANLLED_ERR = -911;//微信未安装
    int NOT_SUPPORT_ERR = -122;//不支持版本
    int READING_TO_SHARE = -999;//已经提交分享长图
}
