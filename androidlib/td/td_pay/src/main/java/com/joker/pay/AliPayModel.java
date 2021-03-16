package com.joker.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.PayTask;
import com.joker.PayFlags;
import com.joker.PayType;
import com.joker.connect.PayCallBack;
import com.joker.model.AliPayResult;
import com.joker.model.PayErrorCode;
import com.joker.model.PayFailure;
import com.joker.model.PaySuccess;
import com.joker.model.order.AliOrder;

import java.lang.ref.WeakReference;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

class AliPayModel extends PayModel {
    private static final int SDK_PAY_FLAG = 1;

    private AliOrder aliOrder;
    private PayHandler payHandler;

    AliPayModel(PayCallBack callBack, Activity activity, @PayFlags int type, AliOrder order) {
        super(callBack, activity, type);
        this.aliOrder = order;
        payHandler = new PayHandler(activity, callBack, this);
    }

    @Override
    public void perform() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(activity);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(aliOrder.pay_info, true);

                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    payHandler.setOrderSN(aliOrder.orderSn);
                    payHandler.sendMessage(msg);
                    unregisterLifecycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @SuppressLint("HandlerLeak")
    private class PayHandler extends Handler {
        private WeakReference<Context> mActivityReference;
        private PayCallBack callBack;
        private AliPayModel aliPayModel;
        private String orderSN;


        PayHandler(Context activity, @NonNull PayCallBack back, AliPayModel aliPayModel) {
            mActivityReference = new WeakReference<>(activity);
            this.callBack = back;
            this.aliPayModel = aliPayModel;
        }

        void setOrderSN(String orderSN) {
            this.orderSN = orderSN;
        }

        @Override
        public void handleMessage(Message msg) {
            final Context activity = mActivityReference.get();
            if (activity != null && msg.what == SDK_PAY_FLAG) {
                aliPayModel.unregisterLifecycle();
                AliPayResult payResult = new AliPayResult((String) msg.obj);
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//                String resultInfo = payResult.getResult();

                String resultStatus = payResult.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    callBack.onSuccess(new PaySuccess(PayType.alipay, orderSN));
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        callBack.onFailure(new PayFailure(PayType.alipay, orderSN, PayErrorCode.Pay_Delay_Time_code, "支付确认中", ""));
                    } else {
                        String err_msg = "";
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        if (!TextUtils.isEmpty(payResult.getMemo())) {
                            err_msg = payResult.getMemo();
                        }
                        if (callBack != null) {
                            if (TextUtils.equals(resultStatus, "6001")) {
                                callBack.onFailure(new PayFailure(PayType.alipay, orderSN, PayErrorCode.Pay_User_Cancel_code, "取消支付", err_msg));
                            } else {
                                callBack.onFailure(new PayFailure(PayType.alipay, orderSN, resultStatus, "支付失败", err_msg));
                            }
                        }
                    }
                }
            }

        }


    }
}
