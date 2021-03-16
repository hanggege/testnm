package com.joker.model;

/**
 * Created by joker on 16/8/10.
 */
public interface PayErrorCode {
    String Pay_Delay_Time_code = "80000";//"8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认
    //"8001”代表第三方已经支付成功，但没有返回相应的数据，第三方问题（主要针对paypal，没办法查服务器，所以将它作为支付出错，提示用户查看订单）
    String Pay_Not_reback_Info_code = "80001";
    String Pay_User_Cancel_code = "80002";//用户取消
    String pay_failure_code = "80003";//支付失败
    //WEIXIN
    String NOT_INSTANLLED_ERR = "80004";//微信未安装
    String NOT_SUPPORT_ERR = "80005";//不支持版本
    String BACK_OF_ERR = "80006";//回调未获得信息

}
