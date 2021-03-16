package com.mei.intimate.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.scwang.smart.drawable.ProgressDrawable;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import org.jetbrains.annotations.NotNull;

/**
 * 经典下拉头部
 * Created by scwang on 2017/5/28.
 */
public class CustomHeader extends LinearLayout implements RefreshHeader {

    private TextView mHeaderText;//标题文本
    private ImageView mProgressView;//刷新动画视图
    private ProgressDrawable mProgressDrawable;//刷新动画

    private String headerText = "倾诉解惑·知心·知你";

    public CustomHeader(Context context) {
        super(context);
        initView(context);
    }

    public CustomHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context);
    }

    public CustomHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);

        mHeaderText = new TextView(context);
        LinearLayout.LayoutParams headerLayoutParams = generateDefaultLayoutParams();
        headerLayoutParams.width = LayoutParams.WRAP_CONTENT;
        headerLayoutParams.height = LayoutParams.WRAP_CONTENT;
        headerLayoutParams.setMargins(0, SmartUtil.dp2px(10), 0, SmartUtil.dp2px(20));
        mHeaderText.setLayoutParams(headerLayoutParams);

        mProgressDrawable = new ProgressDrawable();
        mProgressView = new ImageView(context);
        mProgressView.setImageDrawable(mProgressDrawable);
        LinearLayout.LayoutParams progressLayoutParams = generateDefaultLayoutParams();
        progressLayoutParams.width = SmartUtil.dp2px(20);
        progressLayoutParams.height = SmartUtil.dp2px(20);
        progressLayoutParams.setMargins(0, SmartUtil.dp2px(20), 0, 0);
        mProgressView.setLayoutParams(progressLayoutParams);

        addView(mProgressView);
        addView(mHeaderText);
        setMinimumHeight(SmartUtil.dp2px(60));
    }

    public void setHeaderText(String text) {
        headerText = text;
        mHeaderText.setText(text);
    }

    public String getHeaderText() {
        return headerText;
    }

    @NonNull
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }

    @NotNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void onStartAnimator(@NotNull RefreshLayout layout, int headHeight, int maxDragHeight) {
        mProgressDrawable.start();//开始动画
    }

    @Override
    public int onFinish(@NotNull RefreshLayout layout, boolean success) {
        mProgressDrawable.stop();//停止动画
        mHeaderText.setText(headerText);
        return 300;//延迟100毫秒之后再弹回
    }

    @Override
    public void onStateChanged(@NotNull RefreshLayout refreshLayout, @NotNull RefreshState oldState, @NotNull RefreshState newState) {
        mHeaderText.setText(headerText);
        mProgressView.setVisibility(VISIBLE);
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onInitialized(@NotNull RefreshKernel kernel, int height, int maxDragHeight) {
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void setPrimaryColors(@ColorInt int... colors) {
    }
}