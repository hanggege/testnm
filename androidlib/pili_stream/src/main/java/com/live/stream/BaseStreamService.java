package com.live.stream;

import android.app.Service;

import com.live.stream.module.StreamInfo;
import com.live.stream.presenter.StreamPresenter;
import com.live.stream.presenter.StreamView;
import com.live.stream.presenter.StreamViewKt;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/26
 */
public abstract class BaseStreamService<Info extends StreamInfo> extends Service implements
        StreamPresenter<Info>,
        StreamingStateChangedListener {
    protected boolean isStreaming = false;
    protected boolean isReady = false;
    protected int ioErrorAccount = 0;
    protected Info playInfo;
    /**
     * 服务只有一个StreamView 后续在业务层进行封装分发
     **/
    protected StreamView streamView;


    private MediaStreamingManager mediaStreamingManager;

    public MediaStreamingManager provideStreamingManager() {
        if (mediaStreamingManager == null) {
            mediaStreamingManager = new MediaStreamingManager(this, AVCodecType.SW_AUDIO_CODEC);
            // 当 enabled 设置为 true ，SDK Native 层的 log 将会被打开；当设置为 false，SDK Native 层的 log 将会被关闭。默认处于打开状态。
            mediaStreamingManager.setNativeLoggingEnabled(false);
            // 设置推流状态变化监听
            mediaStreamingManager.setStreamingStateListener(this);
        }
        return mediaStreamingManager;
    }

    private StreamingProfile profile;

    public StreamingProfile provideStreamingProfile() {
        if (profile == null) {
            profile = new StreamingProfile();
            //设置 audio 的 sample rate/audio bitrate
            profile.setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2);
            // 质量优先，实际的码率可能高于设置的码率
            profile.setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY);
        }

        return profile;
    }

    public boolean setPublishUrl(String url, boolean isUrlChange) {
        try {
            provideStreamingProfile().setPublishUrl(url);
            if (isUrlChange) {
                provideStreamingManager().setStreamingProfile(provideStreamingProfile());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @SuppressWarnings("DanglingJavadoc")
    @Override
    public void onStateChanged(StreamingState streamingState, Object o) {
        switch (streamingState) {
            case READY:
                isReady = true;
                /** 准备完成**/
                startStreamingInternal();
                break;
            case STREAMING:
                isStreaming = true;
                StreamViewKt.runOnUiThread(() -> {
                    if (streamView != null) streamView.onStreaming(true);
                });
                break;
            case IOERROR:
                isStreaming = false;
                ioErrorAccount++;
                /** 连接5次都还是这个错误码，可以尝试重新初始化,有可能资源被释放，无法准备好播放信息 **/
                if (ioErrorAccount > 5) {
                    isReady = false;
                    ioErrorAccount = 0;
                }
                break;
            case AUDIO_RECORDING_FAIL:
            case INVALID_STREAMING_URL:
            case UNAUTHORIZED_STREAMING_URL:
            case SHUTDOWN:
            case DISCONNECTED:
                isStreaming = false;
                break;

            default:
                break;
        }
        StreamViewKt.runOnUiThread(() -> {
            if (streamView != null) streamView.onStreamStatus(streamingState, o);
        });
    }

    /**
     * 在进入ready状态之后才正式开始推流
     */
    @SuppressWarnings("DanglingJavadoc")
    private void startStreamingInternal() {
        /** mMediaStreamingManager.startStreaming();  的操作 在 ready 后再稍微 delay 一下 **/
        StreamViewKt.runDelayed(500, () -> {
            /** 开始推流,推流不能在主线程中**/
            boolean success = provideStreamingManager().startStreaming();
            /** 连线失败就回调界面去，成功标志则在onStreamingState回调里面**/
            if (!success) {
                isStreaming = false;
                StreamViewKt.runOnUiThread(() -> {
                    if (streamView != null) streamView.onStreaming(false);
                });
            }
        });

    }


    /*****以下为可操作方法******/

    @Override
    public void bindView(StreamView view) {
        streamView = view;

    }

    @Override
    public void unBindView(StreamView view) {
        streamView = null;
    }

    @Override
    public void start(Info info) {
        setPublishUrl(info.url(), playInfo == null || !playInfo.url().equals(info.url()));
        /**   更新推流地址 再推流 https://developer.qiniu.com/pili/kb/2523/android-change-streaming-params
         需在 onStateChanged READY 状态触发之后推流才会成功。 **/
        if (isReady) {
            startStreamingInternal();
        } else {
            boolean prepared = provideStreamingManager().prepare(provideStreamingProfile());
            boolean resumed = provideStreamingManager().resume();
        }
        playInfo = info;
    }

    @Override
    public void stop() {
        try {
            boolean isSuccess = provideStreamingManager().stopStreaming();
            playInfo = null;
            StreamViewKt.runOnUiThread(() -> {
                if (streamView != null) streamView.onStopStreaming(isSuccess);
            });
        } catch (Exception e) {
//            e.printStackTrace();
        }


    }

    @Override
    public boolean isStreaming() {
        return isStreaming;
    }

    @Override
    public Info getPlayInfo() {
        return playInfo;
    }

    @Override
    public void resetReady() {
        isReady = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isReady = false;
        isStreaming = false;
        playInfo = null;
        if (mediaStreamingManager != null) {
            stop();
            mediaStreamingManager.pause();
            mediaStreamingManager.destroy();
            mediaStreamingManager = null;
        }

    }
}