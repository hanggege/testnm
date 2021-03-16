package com.mei.faceunity.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.faceunity.FURenderer;
import com.faceunity.encoder.MediaAudioEncoder;
import com.faceunity.encoder.MediaEncoder;
import com.faceunity.encoder.MediaMuxerWrapper;
import com.faceunity.encoder.MediaVideoEncoder;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;
import com.mei.faceunity.renderer.CameraRenderer;
import com.mei.faceunity.ui.RecordBtn;
import com.mei.faceunity.utils.NotchInScreenUtil;
import com.mei.faceunity.utils.ThreadHelper;
import com.mei.faceunity.utils.ToastUtil;
import com.mei.wood.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Base Activity, 主要封装FUBeautyActivity与FUEffectActivity的公用界面与方法
 * CameraRenderer相关回调实现
 * Created by tujh on 2018/1/31.
 */
public abstract class FUBaseActivity extends AppCompatActivity
        implements CameraRenderer.OnRendererStatusListener,
        SensorEventListener,
        FURenderer.OnFUDebugListener,
        FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = FUBaseActivity.class.getSimpleName();

    protected GLSurfaceView mGLSurfaceView;
    protected CameraRenderer mCameraRenderer;
    protected volatile boolean isDoubleInputType = true;
    protected TextView mIsTrackingText;
    protected RecordBtn mTakePicBtn;
    protected ViewStub mBottomViewStub;
    //    protected CameraFocus mCameraFocus;
    protected ConstraintLayout mClOperationView;
    protected ConstraintLayout mRootView;
    protected FURenderer mFURenderer;
    protected byte[] mFuNV21Byte;
    protected volatile boolean mTakePicing = false;
    protected volatile boolean mIsNeedTakePic = false;
    /**
     * 录制封装回调
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {

        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                Log.d(TAG, "onPrepared: tid:" + Thread.currentThread().getId());
                mGLSurfaceView.queueEvent(() -> {
                    MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                    videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                    mVideoEncoder = videoEncoder;
                });
                runOnUiThread(() -> mTakePicBtn.setSecond(0));
            }
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            mCountDownLatch.countDown();
            // Call when MediaVideoEncoder's callback and MediaAudioEncoder's callback both are called.
            if (mCountDownLatch.getCount() == 0) {
                Log.d(TAG, "onStopped: tid:" + Thread.currentThread().getId());
                // onStopped is called on codec thread, it may be interrupted, so we execute following code async.
                ThreadHelper.getInstance().execute(() -> {
                    try {
                        final File dcimFile = new File(Constant.cameraFilePath, mVideoOutFile.getName());
                        FileUtils.copyFile(mVideoOutFile, dcimFile);
                        runOnUiThread(() -> {
                            mTakePicBtn.setSecond(mStartTime = 0);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dcimFile)));
                            ToastUtil.showToast(FUBaseActivity.this, R.string.save_video_success);
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "copyFile: ", e);
                    }
                });
            }
        }
    };
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private volatile long mStartTime = 0;
    private File mVideoOutFile;
    private MediaMuxerWrapper mMuxer;
    private volatile MediaVideoEncoder mVideoEncoder;
    private CountDownLatch mCountDownLatch;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~拍照录制部分~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    protected BitmapUtil.OnReadBitmapListener mOnReadBitmapListener = bitmap -> {
        String name = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".png";
        String result = MiscUtil.saveBitmap(bitmap, Constant.photoFilePath, name);
        if (result != null) {
            runOnUiThread(() -> ToastUtil.showToast(FUBaseActivity.this, R.string.save_photo_success));
            File resultFile = new File(result);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(resultFile)));
        }
        mTakePicing = false;
    };

    protected abstract void onCreate();

    protected abstract FURenderer initFURenderer();

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FURenderer信息回调~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onResume() {
        super.onResume();
        mCameraRenderer.onCreate();
        mCameraRenderer.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FURenderer调用部分~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mCameraRenderer.onPause();
        mCameraRenderer.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
                    mFURenderer.setTrackOrientation(x > 0 ? 0 : 180);
                } else {
                    mFURenderer.setTrackOrientation(y > 0 ? 90 : 270);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onFpsChange(final double fps, final double renderTime) {
    }

    @Override
    public void onTrackingStatusChanged(final int status) {
        runOnUiThread(() -> mIsTrackingText.setVisibility(status > 0 ? View.INVISIBLE : View.VISIBLE));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onSurfaceCreated() {
        mFURenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {
    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mvpMatrix, long timeStamp) {
        int fuTextureId = 0;
        if (isDoubleInputType) {
            fuTextureId = mFURenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
        } else if (cameraNV21Byte != null) {
            if (mFuNV21Byte == null || mFuNV21Byte.length != cameraNV21Byte.length) {
                mFuNV21Byte = new byte[cameraNV21Byte.length];
            }
            System.arraycopy(cameraNV21Byte, 0, mFuNV21Byte, 0, cameraNV21Byte.length);
            fuTextureId = mFURenderer.onDrawFrame(mFuNV21Byte, cameraWidth, cameraHeight);
        }
        if (CameraRenderer.DRAW_LANDMARK) {
            mCameraRenderer.setLandmarksData(mFURenderer.getLandmarksData(0));
        }
        sendRecordingData(fuTextureId, mvpMatrix, timeStamp / Constant.NANO_IN_ONE_MILLI_SECOND);
        checkPic(fuTextureId, mvpMatrix, cameraHeight, cameraWidth);
        return fuTextureId;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
    }

    @Override
    public void onCameraChange(int cameraType, int cameraOrientation) {
        mFURenderer.onCameraChange(cameraType, cameraOrientation);
    }

    public void takePic() {
        if (mTakePicing) {
            return;
        }
        mIsNeedTakePic = true;
        mTakePicing = true;
    }

    /**
     * 拍照
     *
     * @param textureId
     * @param mtx
     * @param texWidth
     * @param texHeight
     */
    protected void checkPic(int textureId, float[] mtx, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        BitmapUtil.glReadBitmap(textureId, mtx, GlUtil.IDENTITY_MATRIX, texWidth, texHeight, mOnReadBitmapListener
                , false);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fu_base);
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.fu_base_gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGLVersion(this));
        mCameraRenderer = new CameraRenderer(this, mGLSurfaceView, this);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mIsTrackingText = (TextView) findViewById(R.id.fu_base_is_tracking_text);
        mTakePicBtn = (RecordBtn) findViewById(R.id.fu_base_take_pic);
        mTakePicBtn.setOnRecordListener(new RecordBtn.OnRecordListener() {
            @Override
            public void takePic() {
                FUBaseActivity.this.takePic();
            }

            @Override
            public void startRecord() {
                startRecording();
            }

            @Override
            public void stopRecord() {
                stopRecording();
            }
        });
        mClOperationView = (ConstraintLayout) findViewById(R.id.cl_custom_view);
        mRootView = (ConstraintLayout) findViewById(R.id.cl_root);
        mBottomViewStub = (ViewStub) findViewById(R.id.fu_base_bottom);
        mBottomViewStub.setInflatedId(R.id.fu_base_bottom);
        findViewById(R.id.iv_left_return).setOnClickListener(v -> leftReturn());

        mFURenderer = initFURenderer();
        onCreate();
    }

    public void leftReturn() {

    }

    /**
     * 发送录制数据
     *
     * @param texId
     * @param texMatrix
     * @param timeStamp
     */
    protected void sendRecordingData(int texId, final float[] texMatrix, final long timeStamp) {
        if (mVideoEncoder != null) {
            mVideoEncoder.frameAvailableSoon(texId, texMatrix, GlUtil.IDENTITY_MATRIX);
            if (mStartTime == 0) {
                mStartTime = timeStamp;
            }
            runOnUiThread(() -> mTakePicBtn.setSecond(timeStamp - mStartTime));
        }
    }

    /**
     * 开始录制
     */
    private void startRecording() {
        try {
            mCountDownLatch = new CountDownLatch(2);
            String videoFileName = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".mp4";
            mVideoOutFile = new File(FileUtils.getExternalCacheDir(this), videoFileName);
            mMuxer = new MediaMuxerWrapper(mVideoOutFile.getAbsolutePath());

            // for video capturing
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraRenderer.getCameraHeight(), mCameraRenderer.getCameraWidth());
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);

            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * 停止录制
     */
    private void stopRecording() {
        if (mMuxer != null) {
            mVideoEncoder = null;
            mMuxer.stopRecording();
            Log.d(TAG, "stopRecording: ");
            mMuxer = null;
        }
    }
}
