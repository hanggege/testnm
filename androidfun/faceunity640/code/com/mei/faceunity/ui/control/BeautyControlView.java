package com.mei.faceunity.ui.control;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Filter;
import com.faceunity.param.BeautificationParam;
import com.mei.faceunity.FaceUnityKt;
import com.mei.faceunity.entity.BeautyParameterModel;
import com.mei.faceunity.entity.FilterEnum;
import com.mei.faceunity.ui.CheckGroup;
import com.mei.faceunity.ui.beautybox.BaseBeautyBox;
import com.mei.faceunity.ui.beautybox.BeautyBox;
import com.mei.faceunity.ui.beautybox.BeautyBoxGroup;
import com.mei.faceunity.ui.dialog.BaseDialogFragment;
import com.mei.faceunity.ui.dialog.ConfirmDialogFragment;
import com.mei.faceunity.ui.seekbar.DiscreteSeekBar;
import com.mei.faceunity.utils.OnMultiClickListener;
import com.mei.faceunity.utils.ToastUtil;
import com.mei.widget.round.RoundImageView;
import com.mei.wood.R;

import java.util.List;

import static com.mei.faceunity.entity.BeautyParameterModel.getValue;
import static com.mei.faceunity.entity.BeautyParameterModel.isOpen;
import static com.mei.faceunity.entity.BeautyParameterModel.sFilter;
import static com.mei.faceunity.entity.BeautyParameterModel.sFilterLevel;
import static com.mei.faceunity.entity.BeautyParameterModel.sSkinDetect;
import static com.mei.faceunity.entity.BeautyParameterModel.setValue;


/**
 * Created by tujh on 2017/8/15.
 */
public class BeautyControlView extends FrameLayout {
    private static final String TAG = "BeautyControlView";
    private final Context mContext;
    private OnFUControlListener mOnFUControlListener;
    private CheckGroup mBottomCheckGroup;
    private FrameLayout mFlFaceSkinItems;
    private BeautyBoxGroup mSkinBeautyBoxGroup;
    private BeautyBoxGroup mFaceShapeBeautyBoxGroup;
    private FrameLayout mFlFaceShapeItems;
    private ImageView mIvRecoverFaceShape;
    private TextView mTvRecoverFaceShape;
    private ImageView mIvRecoverFaceSkin;
    private TextView mTvRecoverFaceSkin;
    private View mBottomView;
    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private RadioGroup mRgBlurType;
    private DiscreteSeekBar mBeautySeekBar;
    private boolean isShown;
    // 默认选中第三个粉嫩
    public volatile int mFilterPositionSelect = 0;
    private BeautyBox boxSkinDetect;
    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;
    private ValueAnimator mBottomLayoutAnimator;

    public BeautyControlView(Context context) {
        this(context, null);
    }

    public BeautyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);
        initView();
    }

    public void setOnFUControlListener(@NonNull OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private void initView() {
        mBottomView = findViewById(R.id.cl_bottom_view);
        mBottomView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initViewBottomRadio();
        initViewSkinBeauty();
        initViewFaceShape();
        initViewFilterRecycler();
        initViewTop();
    }

    public void onResume() {
        //初始化数据
        updateViewSkinBeauty();
        updateViewFaceShape();
        updateViewFilterRecycler();

    }


    @Override
    public boolean isShown() {
        return isShown;
    }

    private void initViewBottomRadio() {
        mBottomCheckGroup = findViewById(R.id.beauty_radio_group);
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            int checkedidOld = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
                if ((checkedId == View.NO_ID || checkedId == checkedidOld) && checkedidOld != View.NO_ID) {
                    int endHeight = (int) getResources().getDimension(R.dimen.x1);
                    int startHeight = (int) getResources().getDimension(R.dimen.x268);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    mRgBlurType.setVisibility(INVISIBLE);
                    isShown = false;
                } else if (checkedId != View.NO_ID && checkedidOld == View.NO_ID) {
                    int startHeight = (int) getResources().getDimension(R.dimen.x1);
                    int endHeight = (int) getResources().getDimension(R.dimen.x268);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    if (checkedId == R.id.beauty_radio_skin_beauty && mSkinBeautyBoxGroup.getCheckedBeautyBoxId() == R.id.beauty_box_blur_level) {
                        mRgBlurType.setVisibility(VISIBLE);
                        seekToSeekBar(getValue(mRgBlurType.getCheckedRadioButtonId()));
                    }
                    isShown = true;
                }
                checkedidOld = checkedId;
            }
        });
    }

    private void updateViewSkinBeauty() {
        onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
        onChangeFaceBeautyLevel(R.id.beauty_box_color_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_red_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_bright);
        onChangeFaceBeautyLevel(R.id.beauty_box_tooth_whiten);
        onChangeFaceBeautyLevel(mRgBlurType.getCheckedRadioButtonId());
    }

    private void initViewSkinBeauty() {
        mFlFaceSkinItems = findViewById(R.id.fl_face_skin_items);
        mRgBlurType = findViewById(R.id.rg_blur_type);
        mRgBlurType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                seekToSeekBar(getValue(checkedId));
                int type = 0;
                int desc = R.string.beauty_box_heavy_blur_clear;
                if (checkedId == R.id.rb_blur_fine) {
                    type = 2;
                    desc = R.string.beauty_box_heavy_blur_fine;
                } else if (checkedId == R.id.rb_blur_hazy) {
                    type = 1;
                    desc = R.string.beauty_box_heavy_blur_hazy;
                }
                BeautyParameterModel.sBlurType = type;
                ToastUtil.showNormalToast(mContext, desc);
                checkFaceSkinChanged();
                ((BeautyBox) findViewById(R.id.beauty_box_blur_level)).setOpen(isOpen(checkedId));
                mOnFUControlListener.onBlurTypeSelected(type);
                mOnFUControlListener.onBlurLevelSelected(getValue(checkedId));
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.BLUR_TYPE, (float) type);
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.BLUR_LEVEL, getValue(checkedId));
            }
        });
        mIvRecoverFaceSkin = findViewById(R.id.iv_recover_face_skin);
        mIvRecoverFaceSkin.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_reset_avatar_model), new BaseDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        // recover params
                        BeautyParameterModel.recoverFaceSkinToDefValue();
                        mRgBlurType.check(R.id.rb_blur_clear);
                        updateViewSkinBeauty();
                        int checkedId = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
                        if (checkedId == R.id.beauty_box_blur_level) {
                            checkedId = mRgBlurType.getCheckedRadioButtonId();
                        }
                        if (checkedId != R.id.beauty_box_skin_detect) {
                            seekToSeekBar(checkedId);
                        }
                        setRecoverFaceSkinEnable(false);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                confirmDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "ConfirmDialogFragmentReset");
            }
        });
        mTvRecoverFaceSkin = findViewById(R.id.tv_recover_face_skin);

        mSkinBeautyBoxGroup = findViewById(R.id.beauty_group_skin_beauty);
        mSkinBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mBeautySeekBar.setVisibility(INVISIBLE);
                if (checkedId == R.id.beauty_box_blur_level) {
                    mRgBlurType.setVisibility(VISIBLE);
                    onChangeFaceBeautyLevel(R.id.rb_blur_fine);
                    onChangeFaceBeautyLevel(R.id.rb_blur_hazy);
                    onChangeFaceBeautyLevel(R.id.rb_blur_clear);
                } else {
                    mRgBlurType.setVisibility(INVISIBLE);
                }
                if (checkedId != R.id.beauty_box_skin_detect) {
                    if (checkedId == R.id.beauty_box_blur_level) {
                        checkedId = mRgBlurType.getCheckedRadioButtonId();
                    }
                    seekToSeekBar(checkedId);
                    onChangeFaceBeautyLevel(checkedId);
                }
            }
        });
        boxSkinDetect = findViewById(R.id.beauty_box_skin_detect);
        boxSkinDetect.setOnOpenChangeListener(new BaseBeautyBox.OnOpenChangeListener() {
            @Override
            public void onOpenChanged(BaseBeautyBox beautyBox, boolean isOpen) {
                sSkinDetect = isOpen ? 1 : 0;
                onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
                checkFaceSkinChanged();
            }
        });

        checkFaceSkinChanged();
    }


    //初始化美肤色数据
    private void updateViewFaceShape() {
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_v);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_narrow);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_small);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_chin);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_forehead);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_nose);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_mouth);
    }

    private void initViewFilterRecycler() {
        mFilterRecyclerView = findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter());
        ((SimpleItemAnimator) mFilterRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    /**
     * 过滤
     */
    private void updateViewFilterRecycler() {
        mOnFUControlListener.onFilterNameSelected(sFilter.filterName());
        FaceUnityKt.getCacheFaceFilterParams().put(BeautificationParam.FILTER_NAME, sFilter.filterName());
        float filterLevel = getFilterLevel(sFilter.filterName());
        mOnFUControlListener.onFilterLevelSelected(filterLevel);
        FaceUnityKt.getCacheFaceFilterParams().put(BeautificationParam.FILTER_LEVEL, filterLevel);
    }

    private void initViewFaceShape() {
        mFlFaceShapeItems = findViewById(R.id.fl_face_shape_items);
        mIvRecoverFaceShape = findViewById(R.id.iv_recover_face_shape);
        mIvRecoverFaceShape.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_reset_avatar_model), new BaseDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        // recover params
                        BeautyParameterModel.recoverFaceShapeToDefValue();
                        updateViewFaceShape();
                        int checkedId = mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId();
                        seekToSeekBar(checkedId);
                        setRecoverFaceShapeEnable(false);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                confirmDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "ConfirmDialogFragmentReset");
            }
        });
        mTvRecoverFaceShape = findViewById(R.id.tv_recover_face_shape);
        mFaceShapeBeautyBoxGroup = findViewById(R.id.beauty_group_face_shape);
        mFaceShapeBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mBeautySeekBar.setVisibility(GONE);
                seekToSeekBar(checkedId);
                onChangeFaceBeautyLevel(checkedId);
            }
        });
        checkFaceShapeChanged();
    }

    //如果美型被修改过，恢复按钮会发生变化
    private void setRecoverFaceShapeEnable(boolean enable) {
        if (enable) {
            mIvRecoverFaceShape.setAlpha(1f);
            mTvRecoverFaceShape.setAlpha(1f);
        } else {
            mIvRecoverFaceShape.setAlpha(0.6f);
            mTvRecoverFaceShape.setAlpha(0.6f);
        }
        mIvRecoverFaceShape.setEnabled(enable);
        mTvRecoverFaceShape.setEnabled(enable);
    }

    //如果美肤被修改过，恢复按钮会发生变化
    private void setRecoverFaceSkinEnable(boolean enable) {
        if (enable) {
            mIvRecoverFaceSkin.setAlpha(1f);
            mTvRecoverFaceSkin.setAlpha(1f);
        } else {
            mIvRecoverFaceSkin.setAlpha(0.6f);
            mTvRecoverFaceSkin.setAlpha(0.6f);
        }
        mIvRecoverFaceSkin.setEnabled(enable);
        mTvRecoverFaceSkin.setEnabled(enable);
    }

    //更改后修改参数
    private void onChangeFaceBeautyLevel(int viewId) {
        if (viewId == View.NO_ID) {
            return;
        }
        View view = findViewById(viewId);
        if (view instanceof BaseBeautyBox) {
            boolean open;
            if (viewId == R.id.beauty_box_blur_level) {
                open = isOpen(mRgBlurType.getCheckedRadioButtonId());
            } else {
                open = isOpen(viewId);
            }
            ((BaseBeautyBox) view).setOpen(open);
        }
        if (mOnFUControlListener == null) {
            return;
        }
        switch (viewId) {
            case R.id.beauty_box_skin_detect:
                mOnFUControlListener.onSkinDetectSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.SKIN_DETECT, getValue(viewId));
                break;
            case R.id.beauty_box_blur_level:
                mOnFUControlListener.onBlurLevelSelected(getValue(mRgBlurType.getCheckedRadioButtonId()));
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.BLUR_LEVEL, getValue(mRgBlurType.getCheckedRadioButtonId()));
                break;
            case R.id.rb_blur_clear:
            case R.id.rb_blur_fine:
            case R.id.rb_blur_hazy:
                mOnFUControlListener.onBlurTypeSelected(BeautyParameterModel.sBlurType);
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.BLUR_TYPE, BeautyParameterModel.sBlurType);
                break;
            case R.id.beauty_box_color_level:
                mOnFUControlListener.onColorLevelSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.COLOR_LEVEL, getValue(viewId));
                break;
            case R.id.beauty_box_red_level:
                mOnFUControlListener.onRedLevelSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.RED_LEVEL, getValue(viewId));
                break;
            case R.id.beauty_box_eye_bright:
                mOnFUControlListener.onEyeBrightSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.EYE_BRIGHT, getValue(viewId));
                break;
            case R.id.beauty_box_tooth_whiten:
                mOnFUControlListener.onToothWhitenSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceSkinParams().put(BeautificationParam.TOOTH_WHITEN, getValue(viewId));
                break;
            case R.id.beauty_box_eye_enlarge:
                mOnFUControlListener.onEyeEnlargeSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.EYE_ENLARGING, getValue(viewId));
                break;
            case R.id.beauty_box_cheek_thinning:
                mOnFUControlListener.onCheekThinningSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.CHEEK_THINNING, getValue(viewId));
                break;
            case R.id.beauty_box_cheek_narrow:
                mOnFUControlListener.onCheekNarrowSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.CHEEK_NARROW, getValue(viewId));
                break;
            case R.id.beauty_box_cheek_v:
                mOnFUControlListener.onCheekVSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.CHEEK_V, getValue(viewId));
                break;
            case R.id.beauty_box_cheek_small:
                mOnFUControlListener.onCheekSmallSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.CHEEK_SMALL, getValue(viewId));
                break;
            case R.id.beauty_box_intensity_chin:
                mOnFUControlListener.onIntensityChinSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.INTENSITY_CHIN, getValue(viewId));
                break;
            case R.id.beauty_box_intensity_forehead:
                mOnFUControlListener.onIntensityForeheadSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.INTENSITY_FOREHEAD, getValue(viewId));
                break;
            case R.id.beauty_box_intensity_nose:
                mOnFUControlListener.onIntensityNoseSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.INTENSITY_NOSE, getValue(viewId));
                break;
            case R.id.beauty_box_intensity_mouth:
                mOnFUControlListener.onIntensityMouthSelected(getValue(viewId));
                FaceUnityKt.getCacheFaceShapeParams().put(BeautificationParam.INTENSITY_MOUTH, getValue(viewId));
                break;
            default:
        }
    }

    private void initViewTop() {
        mBeautySeekBar = findViewById(R.id.beauty_seek_bar);
        //美颜值修改
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                Log.i(TAG, valueF + "");
                int checkedCheckBoxId = mBottomCheckGroup.getCheckedCheckBoxId();
                if (checkedCheckBoxId == R.id.beauty_radio_skin_beauty) {
                    //美肤选项
                    int skinCheckedId = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
                    if (skinCheckedId == R.id.beauty_box_blur_level) {
                        setValue(mRgBlurType.getCheckedRadioButtonId(), valueF);
                    } else {
                        setValue(skinCheckedId, valueF);
                    }
                    onChangeFaceBeautyLevel(skinCheckedId);
                    checkFaceSkinChanged();
                } else if (checkedCheckBoxId == R.id.beauty_radio_face_shape) {
                    //美型选项
                    setValue(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                    checkFaceShapeChanged();
                } else if (checkedCheckBoxId == R.id.beauty_radio_filter) {
                    mFilterRecyclerAdapter.setFilterLevels(valueF);
                }
            }
        });
    }

    private void checkFaceShapeChanged() {
        setRecoverFaceShapeEnable(BeautyParameterModel.checkIfFaceShapeChanged());
    }

    private void checkFaceSkinChanged() {
        setRecoverFaceSkinEnable(BeautyParameterModel.checkIfFaceSkinChanged());
    }

    /**
     * 点击底部 tab
     *
     * @param viewId
     */
    private void clickViewBottomRadio(int viewId) {
        mFlFaceShapeItems.setVisibility(GONE);
        mFlFaceSkinItems.setVisibility(GONE);
        mFilterRecyclerView.setVisibility(GONE);
        mBeautySeekBar.setVisibility(GONE);
        if (viewId == R.id.beauty_radio_skin_beauty) {
            mFlFaceSkinItems.setVisibility(VISIBLE);
            int id = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
            if (id != R.id.beauty_box_skin_detect) {
                if (id == R.id.beauty_box_blur_level) {
                    id = mRgBlurType.getCheckedRadioButtonId();
                    mRgBlurType.setVisibility(VISIBLE);
                }
                seekToSeekBar(id);
            }
        } else if (viewId == R.id.beauty_radio_face_shape) {
            mFlFaceShapeItems.setVisibility(VISIBLE);
            int id = mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId();
            seekToSeekBar(id);
            mRgBlurType.setVisibility(INVISIBLE);
        } else if (viewId == R.id.beauty_radio_filter) {
            mFilterRecyclerView.setVisibility(VISIBLE);
            mFilterRecyclerAdapter.setFilterProgress();
            mRgBlurType.setVisibility(INVISIBLE);
        }
    }

    private void seekToSeekBar(float value) {
        seekToSeekBar(value, 0, 100);
    }

    private void seekToSeekBar(float value, int min, int max) {
        mBeautySeekBar.setVisibility(VISIBLE);
        mBeautySeekBar.setMin(min);
        mBeautySeekBar.setMax(max);
        mBeautySeekBar.setProgress((int) (value * (max - min) + min));
    }

    private void seekToSeekBar(int checkedId) {
        if (checkedId == View.NO_ID) {
            return;
        }
        float value = getValue(checkedId);
        int min = 0;
        int max = 100;
        if (checkedId == R.id.beauty_box_intensity_chin || checkedId == R.id.beauty_box_intensity_forehead || checkedId == R.id.beauty_box_intensity_mouth) {
            min = -50;
            max = 50;
        }
        seekToSeekBar(value, min, max);
    }

    private void changeBottomLayoutAnimator(final int startHeight, final int endHeight) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(150);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mBottomView.getLayoutParams();
                params.height = height;
                mBottomView.setLayoutParams(params);
                if (mOnBottomAnimatorChangeListener != null) {
                    float showRate = 1.0f * (height - startHeight) / (endHeight - startHeight);
                    mOnBottomAnimatorChangeListener.onBottomAnimatorChangeListener(startHeight > endHeight ? 1 - showRate : showRate);
                }
            }
        });
        mBottomLayoutAnimator.start();
    }

    /**
     * 设置美颜保存的参数值
     */

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }


    public void setFilterLevel(String filterName, float faceBeautyFilterLevel) {
        sFilterLevel.put(filterName, faceBeautyFilterLevel);
        if (mOnFUControlListener != null) {
            mOnFUControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
        }
        FaceUnityKt.getCacheFaceFilterParams().put(BeautificationParam.FILTER_LEVEL, faceBeautyFilterLevel);

    }

    public float getFilterLevel(String filterName) {
        String key = filterName;
        Float level = sFilterLevel.get(key);
        if (level == null) {
            level = Filter.DEFAULT_FILTER_LEVEL;
            sFilterLevel.put(key, level);
        }

        setFilterLevel(filterName, level);
        return level;
    }

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
    }


    class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.HomeRecyclerHolder> {

        @Override
        public FilterRecyclerAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FilterRecyclerAdapter.HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_control_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(FilterRecyclerAdapter.HomeRecyclerHolder holder, final int position) {
            final List<Filter> filters = FilterEnum.getFiltersByFilterType();
            holder.filterImg.setImageResource(filters.get(position).resId());
            holder.filterName.setText(filters.get(position).description());
            if (mFilterPositionSelect == position) {
                holder.filterImg.setRadius(getResources().getDimensionPixelOffset(R.dimen.x6));
                holder.filterImg.setViewStrokeWidth(getResources().getDimensionPixelOffset(R.dimen.x4));
                holder.filterImg.setViewStrokeColor(ContextCompat.getColor(getContext(), R.color.main_color));
            } else {
                holder.filterImg.setRadius(0);
                holder.filterImg.setViewStrokeWidth(0);
            }
            holder.itemView.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    mBeautySeekBar.setVisibility(position == 0 ? INVISIBLE : VISIBLE);
                    mFilterPositionSelect = position;
                    setFilterProgress();
                    notifyDataSetChanged();
                    if (mOnFUControlListener != null) {
                        sFilter = filters.get(mFilterPositionSelect);
                        mOnFUControlListener.onFilterNameSelected(sFilter.filterName());
                        FaceUnityKt.getCacheFaceFilterParams().put(BeautificationParam.FILTER_NAME, sFilter.filterName());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return FilterEnum.getFiltersByFilterType().size();
        }

        public void setFilterLevels(float filterLevels) {
            if (mFilterPositionSelect > 0) {
                setFilterLevel(FilterEnum.getFiltersByFilterType().get(mFilterPositionSelect).filterName(), filterLevels);
            }
        }

        public void setFilterProgress() {
            if (mFilterPositionSelect > 0) {
                seekToSeekBar(getFilterLevel(FilterEnum.getFiltersByFilterType().get(mFilterPositionSelect).filterName()));
            }
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            RoundImageView filterImg;
            TextView filterName;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                filterImg = itemView.findViewById(R.id.control_recycler_img);
                filterName = itemView.findViewById(R.id.control_recycler_text);
            }
        }
    }

}