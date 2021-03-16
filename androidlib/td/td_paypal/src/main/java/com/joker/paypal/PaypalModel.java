package com.joker.paypal;

import android.app.Activity;
import android.content.Intent;

import com.joker.PayFlags;
import com.joker.PayType;
import com.joker.connect.PayCallBack;
import com.joker.model.PayErrorCode;
import com.joker.model.PayFailure;
import com.joker.model.PaySuccess;
import com.joker.model.order.PalOrder;
import com.joker.pay.PayModel;
import com.joker.support.TdSdk;
import com.joker.support.listener.TdPerFormSuper;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ProofOfPayment;

import java.math.BigDecimal;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/3.
 */

@SuppressWarnings("unused")
public class PaypalModel extends PayModel implements TdPerFormSuper {
    private static final int PAYPAL_REQUEST_CODE_PAYMENT = 111;
    private PalOrder orderInfo;

    private PayPalConfiguration getConfig(Activity activity, String clientID) {
        return new PayPalConfiguration()
                .environment(TdSdk.PaypalProduction(activity))
                .clientId(clientID)
                .merchantName(activity.getString(R.string.app_name));
    }

    public PaypalModel(Activity activity, @PayFlags Integer type, PalOrder order, PayCallBack callBack) {
        super(callBack, activity, type);
        this.orderInfo = order;
    }

    @Override
    public void perform() {
        PayPalConfiguration confirmation = getConfig(activity, orderInfo.paypal_clientID);
        Intent intent = new Intent(activity, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, confirmation);
        activity.startService(intent);

        PayPalPayment thingToBuy = new PayPalPayment(
                new BigDecimal(orderInfo.total_fee),
                orderInfo.currency,
                orderInfo.item_name,
                PayPalPayment.PAYMENT_INTENT_SALE);
        thingToBuy.invoiceNumber(orderInfo.orderSn);

        Intent payIntent = new Intent(activity, PaymentActivity.class);
        payIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, confirmation);
        payIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        activity.startActivityForResult(payIntent, PAYPAL_REQUEST_CODE_PAYMENT);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    ProofOfPayment info = confirm.getProofOfPayment();
                    if (info != null) {
                        callBack.onSuccess(new PaySuccess(PayType.paypal, orderInfo.orderSn,
                                info.getPaymentId(), info.getState(), info.getIntent()));
                    } else {
                        callBack.onFailure(new PayFailure(PayType.paypal, orderInfo.orderSn,
                                PayErrorCode.Pay_Not_reback_Info_code, "请到【我的订单】查看详细", ""));
                    }
                } else {
                    callBack.onFailure(new PayFailure(PayType.paypal, orderInfo.orderSn,
                            PayErrorCode.Pay_Not_reback_Info_code, "请到【我的订单】查看详细", ""));
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                callBack.onFailure(new PayFailure(PayType.paypal, orderInfo.orderSn,
                        PayErrorCode.Pay_User_Cancel_code, "支付失败,请重试", "用户取消"));
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                callBack.onFailure(new PayFailure(PayType.paypal, orderInfo.orderSn,
                        PayErrorCode.pay_failure_code, "支付失败,请重试", ""));
            }
            unregisterLifecycle();
        }
    }
}
