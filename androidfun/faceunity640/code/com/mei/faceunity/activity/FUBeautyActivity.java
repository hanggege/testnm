package com.mei.faceunity.activity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.faceunity.FURenderer;
import com.faceunity.entity.Filter;
import com.faceunity.param.BeautificationParam;
import com.mei.base.network.holder.SpiceHolderImp;
import com.mei.faceunity.FaceUnityKt;
import com.mei.faceunity.entity.BeautyParameterModel;
import com.mei.faceunity.entity.FilterEnum;
import com.mei.faceunity.ui.control.BeautyControlView;
import com.mei.orc.http.retrofit.RetrofitClient;
import com.mei.wood.R;

import java.util.Map;


/**
 * 美颜界面
 * Created by tujh on 2018/1/31.
 */

public class FUBeautyActivity extends FUBaseActivity {
    public final static String TAG = FUBeautyActivity.class.getSimpleName();
    RetrofitClient retrofitClient;
    private BeautyControlView mBeautyControlView;

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beauty);
        mBeautyControlView = (BeautyControlView) mBottomViewStub.inflate();
        mBeautyControlView.setOnFUControlListener(mFURenderer);
        mBeautyControlView.setOnBottomAnimatorChangeListener(new BeautyControlView.OnBottomAnimatorChangeListener() {
            private int px166 = getResources().getDimensionPixelSize(R.dimen.x160);
            private int px156 = getResources().getDimensionPixelSize(R.dimen.x156);
            private int px402 = getResources().getDimensionPixelSize(R.dimen.x402);
            private int diff = px402 - px156;

            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                // 收起 1-->0，弹出 0-->1
                double v = px166 * (1 - showRate * 0.265);
                mTakePicBtn.setDrawWidth((int) v);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
                params.bottomMargin = (int) (px156 + diff * showRate);
                mTakePicBtn.setLayoutParams(params);
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166)
                        * (1 - showRate * 0.265)));
            }
        });

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
        params.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x156);
        int size = getResources().getDimensionPixelSize(R.dimen.x160);
        mTakePicBtn.setLayoutParams(params);
        mTakePicBtn.setDrawWidth(size);
        mTakePicBtn.bringToFront();
        retrofitClient = new SpiceHolderImp().getApiSpiceMgr();
        dealWithFaceUnityInfo(FaceUnityKt.loadFaceUnityInfo());
    }

    @Override
    public void leftReturn() {
        super.leftReturn();
        FaceUnityKt.saveFaceUnityInfo();
        finish();
    }

    /**
     * 处理返回数据
     *
     * @param skinBeauty
     */
    @SuppressWarnings("ConstantConditions")
    private void dealWithFaceUnityInfo(@NonNull Map<String, Object> skinBeauty) {
        if (skinBeauty.isEmpty()) {
            return;
        }
        for (String key : skinBeauty.keySet()) {
            float value = 0;
            if (!key.equals(BeautificationParam.FILTER_NAME)) {
                value = Float.parseFloat(String.valueOf(skinBeauty.get(key)));
            }
            switch (key) {
                // 滤镜
                case BeautificationParam.FILTER_NAME:
                    for (int i = 0; i < FilterEnum.getFiltersByFilterType().size(); i++) {
                        Filter filter = FilterEnum.getFiltersByFilterType().get(i);
                        if (filter.filterName().equals(String.valueOf(value))) {
                            BeautyParameterModel.sFilter = filter;
                            mBeautyControlView.mFilterPositionSelect = i;
                        }
                    }
                    break;
                case BeautificationParam.FILTER_LEVEL:
                    break;
                //美肤
                case BeautificationParam.SKIN_DETECT:
                    BeautyParameterModel.sSkinDetect = value;
                    break;
                case BeautificationParam.BLUR_TYPE:
                    BeautyParameterModel.sBlurType = value;
                    break;
                case BeautificationParam.BLUR_LEVEL:
//                    BeautyParameterModel.sBlurTypeLevels = value
                    break;
                case BeautificationParam.COLOR_LEVEL:
                    BeautyParameterModel.sColorLevel = value;
                    break;
                case BeautificationParam.RED_LEVEL:
                    BeautyParameterModel.sRedLevel = value;
                    break;
                case BeautificationParam.EYE_BRIGHT:
                    BeautyParameterModel.sEyeBright = value;
                    break;
                case BeautificationParam.TOOTH_WHITEN:
                    BeautyParameterModel.sToothWhiten = value;
                    break;

                //美型
                case BeautificationParam.EYE_ENLARGING:
                    BeautyParameterModel.sEyeEnlarging = value;
                    break;
                case BeautificationParam.CHEEK_THINNING:
                    BeautyParameterModel.sCheekThinning = value;
                    break;
                case BeautificationParam.CHEEK_NARROW:
                    BeautyParameterModel.sCheekNarrow = value;
                    break;
                case BeautificationParam.CHEEK_SMALL:
                    BeautyParameterModel.sCheekSmall = value;
                    break;

                case BeautificationParam.CHEEK_V:
                    BeautyParameterModel.sCheekV = value;
                    break;
                case BeautificationParam.INTENSITY_NOSE:
                    BeautyParameterModel.sIntensityNose = value;
                    break;
                case BeautificationParam.INTENSITY_CHIN:
                    BeautyParameterModel.sIntensityChin = value;
                    break;
                case BeautificationParam.INTENSITY_FOREHEAD:
                    BeautyParameterModel.sIntensityForehead = value;
                    break;
                case BeautificationParam.INTENSITY_MOUTH:
                    BeautyParameterModel.sIntensityMouth = value;
                    break;
            }
        }

        runOnUiThread(() -> {
            mBeautyControlView.onResume();
        });
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBeautyControlView != null) {
            mBeautyControlView.onResume();
        }
    }

    @Override
    public void onBackPressed() {
        leftReturn();
        super.onBackPressed();
    }
}
