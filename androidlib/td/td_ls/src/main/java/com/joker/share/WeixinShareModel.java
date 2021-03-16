package com.joker.share;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.DrawableRes;

import com.joker.TdType;
import com.joker.connect.TdCallBack;
import com.joker.constant.TdErrorCode;
import com.joker.flag.ClientFlags;
import com.joker.flag.TdFlags;
import com.joker.model.BackResult;
import com.joker.model.ErrResult;
import com.joker.model.ShareInfo;
import com.joker.support.TdAction;
import com.joker.support.TdRequest;
import com.joker.support.TdSdk;
import com.joker.support.WeixinEvent;
import com.joker.support.listener.TdImageAdapter;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/2.
 */

public class WeixinShareModel extends ShareModel implements Observer {
    private IWXAPI api;
    private TdImageAdapter adapter;

    private IWXAPI getApi(Activity activity) {
        if (api == null) {
            api = TdSdk.getWeixinAPI(activity, TdSdk.WxAppid(activity));
        }
        return api;
    }

    WeixinShareModel(@TdFlags Integer type, Activity activity, ShareInfo info, TdCallBack callBack, TdImageAdapter adapter) {
        super(type, activity, info, callBack);
        WeixinEvent.getInstance().addObserver(this);
        this.adapter = adapter;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @Override
    public void perform() {
        if (!getApi(activity).isWXAppInstalled()) {
            callBack.onFailure(new ErrResult(type, TdErrorCode.NOT_INSTANLLED_ERR, "没有安装微信，请先安装微信"));
        } else {
            getMsg(new TdAction<WXMediaMessage>() {
                @Override
                public void onCallback(WXMediaMessage msg) {
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = "hnh" + System.currentTimeMillis();
                    req.message = msg;
                    req.scene = shareInfo.clientType == TdType.we_chat ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                    api.sendReq(req);
                }
            });
        }
    }

    private void getMsg(final TdAction<WXMediaMessage> back) {
        switch (shareInfo.shareType) {
            case TdType.image_share:
                if (URLUtil.isNetworkUrl(shareInfo.imgUrl) || shareInfo.imgUrl.startsWith("data:image/png;base64,")) {//网络图片
                    getBitmap(shareInfo.imgUrl, shareInfo.resID, new TdAction<Bitmap>() {
                        @Override
                        public void onCallback(Bitmap bitmap) {
                            if (bitmap != null) {
                                WXMediaMessage msg = new WXMediaMessage(new WXImageObject(bitmap));
                                back.onCallback(msg);
                            } else {
                                callBack.onFailure(new ErrResult(type, TdErrorCode.IMAGE_DECODE_ERR, "分享失败，请重试"));
                            }
                        }
                    });
                } else {
                    callBack.onFailure(new ErrResult(type, TdErrorCode.READING_TO_SHARE, "已提交分享"));
                    shareWeChat(shareInfo.imgUrl, shareInfo.clientType);
                }
                break;

            case TdType.multi_share:
                getMultiShare(back);
                break;

            case TdType.mini_program_share:
                getMiniProgram(back);
                break;

            default:
                break;
        }
    }

    private void getMultiShare(final TdAction<WXMediaMessage> back) {
        String limitUrl = shareInfo.imgUrl;
        if (URLUtil.isNetworkUrl(limitUrl) && !shareInfo.imgUrl.contains("?")) {
            limitUrl = shareInfo.imgUrl + "?imageMogr2/format/jpg/size-limit/32k!/thumbnail/200x200";
        }
        getBitmap(limitUrl, shareInfo.resID, new TdAction<Bitmap>() {
            @Override
            public void onCallback(Bitmap bitmap) {
                if (bitmap != null) {
                    checkShareInfo(shareInfo);
                    WXMediaMessage msg = new WXMediaMessage(new WXWebpageObject(shareInfo.shareUrl));
                    msg.title = shareInfo.title;
                    msg.description = shareInfo.content;
                    msg.thumbData = limitBitmap(imageCrop(bitmap), 32768);

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = "hnh" + System.currentTimeMillis();
                    req.message = msg;
                    req.scene = shareInfo.clientType == TdType.we_chat ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                    back.onCallback(msg);
                } else {
                    callBack.onFailure(new ErrResult(type, TdErrorCode.IMAGE_DECODE_ERR, "分享失败，请重试"));
                }
            }
        });
    }


    /**
     * 小程序分享消息
     */
    private void getMiniProgram(final TdAction<WXMediaMessage> back) {
        String limitUrl = shareInfo.imgUrl;
        if (URLUtil.isNetworkUrl(limitUrl) && !shareInfo.imgUrl.contains("?")) {
            limitUrl = shareInfo.imgUrl + "?imageMogr2/format/jpg/size-limit/128k!/thumbnail/200x200";///thumbnail/300x
        }
        getBitmap(limitUrl, shareInfo.resID, new TdAction<Bitmap>() {
            @Override
            public void onCallback(Bitmap bitmap) {
                if (bitmap != null) {
                    checkShareInfo(shareInfo);
                    WXMediaMessage msg;
                    Bitmap finalBitmap;
                    if (shareInfo.miniAppInfo != null && shareInfo.miniAppInfo.mini_program_path != null && shareInfo.miniAppInfo.mini_program_path.length() > 0) {
                        // 分享小程序
                        WXMiniProgramObject miniProgramObject = new WXMiniProgramObject();
                        miniProgramObject.webpageUrl = shareInfo.shareUrl; // 低版本微信将会打开网页
                        miniProgramObject.path = shareInfo.miniAppInfo.mini_program_path;
                        miniProgramObject.userName = shareInfo.miniAppInfo.mini_program_name;
                        msg = new WXMediaMessage(miniProgramObject);
                        finalBitmap = imageCropMiniProgram(bitmap);
                    } else {
                        msg = new WXMediaMessage(new WXWebpageObject(shareInfo.shareUrl));
                        finalBitmap = imageCrop(bitmap);
                    }
                    msg.title = shareInfo.title;
                    msg.description = shareInfo.content;
                    msg.thumbData = limitBitmap(finalBitmap, 131072);
//                    msg.setThumbImage(finalBitmap);

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = "hnh" + System.currentTimeMillis();
                    req.message = msg;
                    req.scene = shareInfo.clientType == TdType.we_chat ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                    back.onCallback(msg);
                } else {
                    callBack.onFailure(new ErrResult(type, TdErrorCode.IMAGE_DECODE_ERR, "分享失败，请重试"));
                }
            }
        });
    }


    /**
     * 限制图片大小
     */
    private byte[] limitBitmap(Bitmap bitmap, long limit) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        /* 当图片太大，依次压缩,找到最适合的大小 **/
        int options = 90;
        while (output.toByteArray().length > limit && options >= 10) {
            output.reset();//清空
            //这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);
            options -= 10;
        }
        bitmap.recycle();
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void getBitmap(String url, @DrawableRes int res, final TdAction<Bitmap> back) {
        if (TextUtils.isEmpty(url)) {
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), res);
            back.onCallback(bitmap);
        } else {
            if (adapter != null) {
                adapter.getImgBitmap(activity, url, back);
            } else {
                TdRequest.getBitmap(url, back);
            }
        }
    }

    @Override
    public void update(Observable o, Object result) {
        if (o instanceof WeixinEvent && result instanceof SendMessageToWX.Resp) {
            SendMessageToWX.Resp info = (SendMessageToWX.Resp) result;
            if (info.errCode == BaseResp.ErrCode.ERR_OK) {
                callBack.onSuccess(new BackResult(type));
            } else {
                int code = info.errCode;
                String msg;
                switch (info.errCode) {
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        msg = "分享失败，取消分享";
                        code = TdErrorCode.USER_CANCEL_ERR;
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        msg = "发送被拒绝";
                        break;
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        msg = "不支持错误";
                        break;
                    case BaseResp.ErrCode.ERR_BAN:
                        msg = "签名错误";
                        break;
                    default:
                        msg = "未知错误";
                        code = TdErrorCode.NONE_CODE_ERR;
                        break;
                }
                callBack.onFailure(new ErrResult(type, code, msg));
            }
        } else {
            callBack.onFailure(new ErrResult(type, TdErrorCode.NONE_CODE_ERR, "分享失败"));
        }
        unregisterLifecycle();
        WeixinEvent.getInstance().deleteObserver(this);
    }

    private void checkShareInfo(ShareInfo shareInfo) {
        if (!TextUtils.isEmpty(shareInfo.title) && shareInfo.title.length() > 512) {
            shareInfo.title = shareInfo.title.substring(0, 512);
        } else if (!TextUtils.isEmpty(shareInfo.content) && shareInfo.content.length() > 1024) {
            shareInfo.content = shareInfo.content.substring(0, 1024);
        }
    }

    /**
     * 按正方形裁切图片
     */
    private static Bitmap imageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        Bitmap newBitmap = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
        return createBitmapThumbnail(newBitmap);
    }


    /**
     * 小程序按宽高比 5:4截图
     */
    private static Bitmap imageCropMiniProgram(Bitmap bitmap) {
        Bitmap newBitmap;
        try {
            int w = bitmap.getWidth(); // 得到图片的宽，高
            int h = bitmap.getHeight();

            // 取不够的
            if ((w * 4 / 5) > h) {
                newBitmap = Bitmap.createBitmap(bitmap, (w / 2 - h / 2), 0, (h * 5 / 4), h, null, false);
            } else {
                newBitmap = Bitmap.createBitmap(bitmap, 0, (h / 2 - w / 2), w, (w * 4 / 5), null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            newBitmap = imageCrop(bitmap);
        }
        return newBitmap;
    }

    /**
     * 图片压缩
     */
    private static Bitmap createBitmapThumbnail(Bitmap bitMap) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 99;
        int newHeight = 99;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
    }

    /**
     * 分享图片到 微信
     *
     * @param path 本地路径的图片
     */
    private void shareWeChat(String path, @ClientFlags int clientType) {
        try {
            Uri uriToImage;
            File imgFile = new File(path);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                uriToImage = Uri.fromFile(imgFile);
            } else {
                //修复微信在7.0崩溃的问题
                uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), imgFile.getAbsolutePath(), imgFile.getName(), null));
            }
            Intent shareIntent = new Intent();
            //发送图片到朋友圈
            ComponentName comp;
            //发送图片给好友。
            if (clientType == TdType.we_chat) {
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            } else {
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            }
            shareIntent.setComponent(comp);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            shareIntent.setType("image/jpeg");
            activity.startActivity(Intent.createChooser(shareIntent, "分享图片"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
