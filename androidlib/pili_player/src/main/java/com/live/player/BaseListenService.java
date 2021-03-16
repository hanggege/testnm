package com.live.player;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.live.player.module.ListenInfo;
import com.live.player.presenter.ListenAudioPresenter;
import com.live.player.presenter.ListenAudioView;
import com.live.player.presenter.ListenView;
import com.live.player.presenter.ListenViewKt;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.PLOnSeekCompleteListener;

import org.jetbrains.annotations.NotNull;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/27
 */
@SuppressWarnings("DanglingJavadoc")
public abstract class BaseListenService<Info extends ListenInfo> extends Service implements
        ListenAudioPresenter<Info>,
        PLOnPreparedListener,
        PLOnInfoListener,
        PLOnErrorListener,
        PLOnCompletionListener,
        PLOnSeekCompleteListener,
        PLOnBufferingUpdateListener {

    protected Info playInfo;
    protected int bufferPercent = 0;//缓存的进度记录
    private int retryIOErrorCount = 0;// 网络出错重试次数

    /**
     * 服务只有一个 {@link ListenAudioView} 后续在业务层进行封装分发,且分为
     **/
    protected ListenView listenAudioView;

    private TelephonyManager telephonyManager;
    private PLMediaPlayer mediaPlayer;

    protected PLMediaPlayer player() {
        if (mediaPlayer == null) {
            AVOptions avOptions = new AVOptions();
            // the unit of timeout is ms
            avOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
            // 1 -> hw codec enable, 0 -> disable [recommended]
            avOptions.setInteger(AVOptions.KEY_MEDIACODEC, 0);
            // 预设置 SDK 的 log 等级， 0-4 分别为 v/d/i/w/e
            avOptions.setInteger(AVOptions.KEY_LOG_LEVEL, 0);
            mediaPlayer = new PLMediaPlayer(this, avOptions);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setLooping(false);
        }
        return mediaPlayer;
    }

    private AVOptions getAVOption(int startPosition) {
        AVOptions avOptions = new AVOptions();
        // the unit of timeout is ms
        avOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        avOptions.setInteger(AVOptions.KEY_MEDIACODEC, 0);
        // 预设置 SDK 的 log 等级， 0-4 分别为 v/d/i/w/e
        avOptions.setInteger(AVOptions.KEY_LOG_LEVEL, 3);
        avOptions.setInteger(AVOptions.KEY_START_POSITION, startPosition * 1000);
        return avOptions;
    }

    @SuppressLint("ServiceCast")
    @Override
    public void onCreate() {
        super.onCreate();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    /***************** ListenPresenter 操作项  多用于直播 ******************/

    @Override
    public void start(@NotNull Info info) {
        try {
            retryIOErrorCount = 0;
            playInfo = null;
            if (player().isPlaying()) player().stop();
            if (TextUtils.isEmpty(info.url())) {
                ListenViewKt.runOnUiThread(() -> {
                    if (listenAudioView != null)
                        listenAudioView.onError(PLOnErrorListener.ERROR_CODE_OPEN_FAILED);
                });
            } else {
                if (info.startPosition() > 0) {
                    player().setAVOptions(getAVOption(info.startPosition()));
                }
                player().setDataSource(info.url());
                player().prepareAsync();
                playInfo = info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        retryIOErrorCount = 0;
        player().stop();
        playInfo = null;
        ListenViewKt.runOnUiThread(() -> {
            if (listenAudioView instanceof ListenAudioView)
                ((ListenAudioView) listenAudioView).onStateChange(false);
            if (listenAudioView != null) listenAudioView.onStopPlayer();
        });
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && player().isPlaying(); //&& !isPlayCompleted();
    }

    /**
     * 播放已经完毕
     * PLOnCompletionListener 经常不执行，没办法，自己先按时长弄一个
     */
//    public boolean isPlayCompleted() {
//        return mediaPlayer != null && Math.abs(getDuration() - getProgress()) < 1000;
//    }
    @Override
    public Info getPlayInfo() {
        return playInfo;
    }

    /***************** ListenAudioPresenter 操作项 多用于播放录音 ******************/

    @Override
    public void pause() {
        retryIOErrorCount = 0;
        if (mediaPlayer != null) {
            player().pause();
            ListenViewKt.runOnUiThread(() -> {
                if (listenAudioView instanceof ListenAudioView)
                    ((ListenAudioView) listenAudioView).onStateChange(false);
            });
        }
    }

    @Override
    public void resume() {
        retryIOErrorCount = 0;
        if (mediaPlayer != null) {
            player().start();
            ListenViewKt.runOnUiThread(() -> {
                if (listenAudioView instanceof ListenAudioView)
                    ((ListenAudioView) listenAudioView).onStateChange(true);
            });
        }

    }

    @Override
    public long getDuration() {
        return mediaPlayer == null ? 0 : player().getDuration();
    }

    @Override
    public long getProgress() {
        return mediaPlayer == null ? 0 : player().getCurrentPosition();
    }

    @Override
    public void seekTo(long progress) {
        if (mediaPlayer != null && progress >= 0) {
            player().seekTo(progress);
        }
    }

    @Override
    public void bindView(@NotNull ListenView view) {
        listenAudioView = view;
    }

    @Override
    public void unBindView(@NotNull ListenView view) {
        listenAudioView = view;
    }


    /***************** 播放器 sdk 回调项 ******************/
    @Override
    public void onBufferingUpdate(int percent) {
        bufferPercent = percent;
        ListenViewKt.runOnUiThread(() -> {
            if (listenAudioView instanceof ListenAudioView)
                ((ListenAudioView) listenAudioView).onBufferingUpdate(percent);
        });

    }

    @Override
    public void onCompletion() {
        /** 这里处理是为了回调 【ERROR_CODE_IO_ERROR】之后进行回显操作 **/
        ListenViewKt.runOnUiThread(() -> {
            if (listenAudioView instanceof ListenAudioView) {
                ((ListenAudioView) listenAudioView).onStateChange(false);
                ((ListenAudioView) listenAudioView).onCompletion();
            }
        });
    }


    @Override
    public boolean onError(int code) {
//        if (retryIOErrorCount < 5 && code == PLOnErrorListener.ERROR_CODE_IO_ERROR) {
//            /** 网络出错重试5次 **/
//            retryIOErrorCount++;
//            player().start();
//            return true;
//        }
        ListenViewKt.runOnUiThread(() -> {
            /** stop 之后不需要关注错误结果 **/
            if (listenAudioView != null && playInfo != null) listenAudioView.onError(code);
        });
        /** 如果播放过程中产生onError，并且没有处理的话，最后会回调本接口{@link #onCompletion} ***/
        if (code == PLOnErrorListener.ERROR_CODE_IO_ERROR && bufferPercent < 100) {
            if (!player().getDataSource().startsWith("rtmp")) {//非直播暂停
                pause();
            }
        } else if (code == PLOnErrorListener.ERROR_CODE_OPEN_FAILED) {
            stop();
        }
        return code != PLOnErrorListener.ERROR_CODE_IO_ERROR;
    }

    @Override
    public void onInfo(int what, int extra) {
        ListenViewKt.runOnUiThread(() -> {
            if (listenAudioView != null) listenAudioView.onInfo(what, extra);
        });
    }

    /**
     * 准备完成，开始播放
     *
     * @param preparedTime
     */
    @Override
    public void onPrepared(int preparedTime) {
        player().start();
        ListenViewKt.runOnUiThread(() -> {
            if (listenAudioView != null) {
                listenAudioView.onPrepared(preparedTime);
                if (listenAudioView instanceof ListenAudioView)
                    ((ListenAudioView) listenAudioView).onStateChange(true);
            }
        });
    }

    /**
     * 拖动进度条加载完成
     */
    @Override
    public void onSeekComplete() {
        ListenViewKt.runOnUiThread(() -> {
            if (listenAudioView instanceof ListenAudioView)
                ((ListenAudioView) listenAudioView).onSeekComplete();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stop();
            mediaPlayer.release();
            mediaPlayer = null;
            playInfo = null;
        }
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            if (state == TelephonyManager.CALL_STATE_OFFHOOK && isPlaying()) {
                player().pause();
            }
        }
    };
}
