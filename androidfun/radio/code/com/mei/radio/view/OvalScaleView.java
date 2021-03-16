package com.mei.radio.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.mei.orc.ext.DimensionsKt;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import kotlin.Suppress;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * @author Created by lenna on 2020/9/22
 * 电台转盘控件
 */
public class OvalScaleView extends View {
    private static final DecimalFormat decimalFormat;
    private static final int paintColor = Color.parseColor("#ffffff");
    /**
     * 线条上的红点颜色
     */
    private static final int lineOvalColor = Color.parseColor("#fe5353");

    static {
        decimalFormat = new DecimalFormat("#.#");
    }

    private float viewWidth;
    private float viewHeight;
    private float marginLeft;
    private int centerLineWidth;
    private int remainWidth;
    //长刻度间距
    private float longLineSpaceWidth;
    //弧线宽度
    private float rightArcLineWidth;
    private float rightArcLine2Width;
    private float rightArcLine3Width;
    private float rightArcLine4Width;
    //弧线透明度
    private int rightArcLineAlpha = 128;
    private int rightArcLine2Alpha = 179;
    private int rightArcLine3Alpha = 39;
    //弧线占据的矩形
    private RectF mRightArcRectF1;
    private RectF mRightArcRectF2;
    private RectF mRightArcRectF3;
    private RectF mRightArcRectF4;
    private float canvasDx;
    private float canvasDy;
    private float lineStartX;
    //第二条线的位置
    private float line2X;
    //第三条线的位置
    private float line3X;
    //第四条线的位置
    private float line4X;
    private int shortCalibrationLineAlpha = 38;
    private int longCalibrationLineAlpha = 179;
    private float shortCalibrationLineWidth;
    private float longCalibrationLineWidth;
    private float shortLineHeight;
    private float longLineHeight;
    private float shortLineSpaceWidth;
    //长刻度角度
    private int longCalibrationAngle = 30;
    private float centerLineOvalWidth;
    //小红点半径
    private float pointRadius;
    private RectF ovalRectF;
    //速度追踪器
    private VelocityTracker velocityTracker;
    private OverScroller overScroller;
    //最大速度
    private int maxVelocity;
    private int minimumFlingVelocity;
    private int scrollMinY;
    private int scrollMaxY;
    //移动的总距离
    private float moveTotalDy = 0f;
    private float lastY = 0f;
    private float totalShortLineCount;
    private Rect mDefCenterYRect = new Rect();
    private boolean isRunAnimator = false;
    private boolean isDownEvent = false;
    private boolean isUpRunAnimator = false;
    //间隔时间
    private long intervalTime = 0L;
    private float textY;
    private Paint mRightArcPaint1;
    private Paint mRightArcPaint2;
    private Paint mRightArcPaint3;
    private Paint mRightArcPaint4;
    //长刻度
    private Paint longCalibrationLinePaint;
    private Paint shortCalibrationLinePain;
    private Paint ovalPaint;
    private Paint textPaint;
    private Vibrator vibrator;
    private Context mContext;
    //随着动画进行变动的值
    private int startWidth;

    public OvalScaleView(Context context) {
        this(context, null);
    }

    public OvalScaleView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public OvalScaleView(Context context, AttributeSet attr, int def) {
        super(context, attr, def);
        mContext = context;
        initData();
        initView();
        initPaint();
        initSize();
        initRectF();
    }


    private void initData() {
        WeakReference<Context> wr = new WeakReference(mContext);
        vibrator = (Vibrator) wr.get().getSystemService(VIBRATOR_SERVICE);
        //长刻度间距
        longLineSpaceWidth = dip2px(51.5F);
        //短刻度间距
        shortLineSpaceWidth = longLineSpaceWidth * 1f / 15f;
        //
        viewWidth = dip2px(195f);
        viewHeight = dip2px(488f);
        marginLeft = dip2px(80f);


        //刻度宽
        shortCalibrationLineWidth = dip2px(2f);
        longCalibrationLineWidth = dip2px(2f);

        //
        rightArcLineWidth = dip2px(1f);
        rightArcLine2Width = dip2px(3f);
        rightArcLine3Width = dip2px(1f);
        rightArcLine4Width = dip2px(3f);

        pointRadius = dip2px(4f);

        centerLineOvalWidth = dip2px(2f);


        shortLineHeight = dip2px(16f);
        longLineHeight = dip2px(42f);


        textY = dip2px(195f);

        //屏幕适配
        centerLineWidth = (int) dip2px(148f);
        remainWidth = (int) dip2px(32f);

    }

    public float dip2px(float paramFloat) {
        return (mContext.getResources().getDisplayMetrics()).density * paramFloat;
    }

    private void canvasArcOrLine(Canvas paramCanvas) {
        paramCanvas.drawArc(mRightArcRectF1, -90f, -180f, false, mRightArcPaint1);
        paramCanvas.drawArc(mRightArcRectF2, -90f, -180f, false, mRightArcPaint2);
        paramCanvas.drawArc(mRightArcRectF3, -90f, -180f, false, mRightArcPaint3);
        canvasToLine(paramCanvas);
        paramCanvas.drawArc(mRightArcRectF4, -90f, -180f, false, mRightArcPaint4);
    }

    private void drawText(Canvas paramCanvas, float paramInt) {
        paramInt = (paramInt - 90) / longCalibrationAngle;
        String str = decimalFormat.format((paramInt * 1.5F + 207.4f));
        paramCanvas.save();
        paramCanvas.translate(-canvasDx, 0f);
        //旋转90度
        paramCanvas.rotate(90f);
        textPaint.getTextBounds(str, 0, str.length(), mDefCenterYRect);
        paramCanvas.drawText(str, 0f, (-(textY - line2X + marginLeft) - longLineHeight - dip2px(8f) + startWidth), textPaint);
        paramCanvas.translate(canvasDx, 0f);
        paramCanvas.restore();
    }

    private void updateView(float paramFloat) {
        int i = Math.round(totalShortLineCount);
        totalShortLineCount += getShortCalibrationCount(paramFloat);
        if (Math.round(totalShortLineCount - i) != 0 && Math.round(totalShortLineCount) % 2 == 0)
            playVibrator();
        invalidate();
    }

    private void fling(int paramInt) {
        overScroller.fling(0, 0, 0, -paramInt, 0, 0, scrollMinY, scrollMaxY);
        invalidate();
    }

    /**
     * 画刻度
     */
    private void canvasCalibration(Canvas paramCanvas) {
        int j = Math.round(Float.parseFloat(decimalFormat.format(totalShortLineCount)));
        paramCanvas.save();
        paramCanvas.translate(canvasDx, canvasDy);
        paramCanvas.rotate(-90f);
        for (int i = -j; i <= 180 - j; i++) {
            if (i % longCalibrationAngle == 0) {
                //画长刻度和刻度值
                drawLongCalibrationLine(paramCanvas);
                drawText(paramCanvas, i);
            } else if (i % 2 == 0) {
                //画端刻度
                drawShortCalibrationLine(paramCanvas);
            }
            paramCanvas.rotate(1f);
        }
        paramCanvas.restore();
    }

    private float getMoveShortLineWidth(float paramFloat) {
        return shortLineSpaceWidth * paramFloat;
    }

    private void drawLongCalibrationLine(Canvas paramCanvas) {
        paramCanvas.drawLine(-line2X - startWidth, 0f, (-line2X + longLineHeight - startWidth), 0f, longCalibrationLinePaint);
    }

    private float getShortCalibrationCount(float paramFloat) {
        return paramFloat / shortLineSpaceWidth;
    }

    private void initView() {
        overScroller = new OverScroller(getContext());
        velocityTracker = VelocityTracker.obtain();
        maxVelocity = 5000;
        minimumFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    private void drawShortCalibrationLine(Canvas paramCanvas) {
        paramCanvas.drawLine((-line2X + dip2px(6f)) - startWidth, 0f, (-line2X + dip2px(6f) + shortLineHeight - startWidth), 0f, shortCalibrationLinePain);
    }


    private void initPaint() {
        //第一根弧线
        mRightArcPaint1 = new Paint();
        mRightArcPaint1.setAntiAlias(true);
        mRightArcPaint1.setColor(paintColor);
        mRightArcPaint1.setStyle(Paint.Style.STROKE);
        mRightArcPaint1.setStrokeWidth(rightArcLineWidth);
        mRightArcPaint1.setAlpha(rightArcLineAlpha);
        //第二根弧线
        mRightArcPaint2 = new Paint();
        mRightArcPaint2.setAntiAlias(true);
        mRightArcPaint2.setColor(paintColor);
        mRightArcPaint2.setStyle(Paint.Style.STROKE);
        mRightArcPaint2.setStrokeWidth(rightArcLine2Width);
        mRightArcPaint2.setAlpha(rightArcLine2Alpha);
        //第三根弧线
        mRightArcPaint3 = new Paint();
        mRightArcPaint3.setAntiAlias(true);
        mRightArcPaint3.setColor(paintColor);
        mRightArcPaint3.setStyle(Paint.Style.STROKE);
        mRightArcPaint3.setStrokeWidth(rightArcLine3Width);
        mRightArcPaint3.setAlpha(rightArcLine3Alpha);
        //第四根弧线
        mRightArcPaint4 = new Paint();
        mRightArcPaint4.setAntiAlias(true);
        mRightArcPaint4.setColor(paintColor);
        mRightArcPaint4.setStyle(Paint.Style.STROKE);
        mRightArcPaint4.setStrokeWidth(rightArcLine4Width);
        int rightArcLine4Alpha = 255;
        mRightArcPaint4.setAlpha(rightArcLine4Alpha);

        //短刻度
        shortCalibrationLinePain = new Paint();
        shortCalibrationLinePain.setAntiAlias(true);
        shortCalibrationLinePain.setColor(paintColor);
        shortCalibrationLinePain.setStyle(Paint.Style.STROKE);
        shortCalibrationLinePain.setStrokeWidth(shortCalibrationLineWidth);
        shortCalibrationLinePain.setAlpha(shortCalibrationLineAlpha);
        //长刻度
        longCalibrationLinePaint = new Paint();
        longCalibrationLinePaint.setAntiAlias(true);
        longCalibrationLinePaint.setColor(paintColor);
        longCalibrationLinePaint.setStyle(Paint.Style.STROKE);
        longCalibrationLinePaint.setStrokeWidth(longCalibrationLineWidth);
        longCalibrationLinePaint.setAlpha(longCalibrationLineAlpha);
        //小圆点和线条画笔
        ovalPaint = new Paint();
        ovalPaint.setAntiAlias(true);
        ovalPaint.setColor(lineOvalColor);
        ovalPaint.setStyle(Paint.Style.FILL);
        ovalPaint.setStrokeWidth(centerLineOvalWidth);
        ovalPaint.setAlpha(255);
        //刻度上的标识
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(paintColor);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(DimensionsKt.sp(this, 10f));
        textPaint.setAlpha(255);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void canvasToLine(Canvas paramCanvas) {
        paramCanvas.save();
        paramCanvas.translate(canvasDx, canvasDy);
        paramCanvas.drawLine((-lineStartX - remainWidth - startWidth), 0f, (-line4X - rightArcLine3Width), 0f, ovalPaint);
        //线条上面的小圆点
        paramCanvas.drawOval(ovalRectF, ovalPaint);
        paramCanvas.restore();
    }

    private void initSize() {
        canvasDx = viewWidth - dip2px(180f) + remainWidth + centerLineWidth + marginLeft;
        canvasDy = viewHeight / 2;
        lineStartX = centerLineWidth + marginLeft;
        line2X = lineStartX - dip2px(16f);
        line3X = line2X - dip2px(32f);
        line4X = line3X - dip2px(55f);
        scrollMinY = -Math.round(longLineSpaceWidth * 24f);
        scrollMaxY = Math.round(longLineSpaceWidth * 24f);
    }

    private void initRectF() {
        mRightArcRectF1 = new RectF((canvasDx - lineStartX), (canvasDy - lineStartX), (canvasDx + lineStartX), (canvasDy + lineStartX));
        mRightArcRectF2 = new RectF((canvasDx - line2X), (canvasDy - line2X), (canvasDx + line2X), (canvasDy + line2X));
        mRightArcRectF3 = new RectF((canvasDx - line3X), (canvasDy - line3X), (canvasDx + line3X), (canvasDy + line3X));
        mRightArcRectF4 = new RectF((canvasDx - line4X), (canvasDy - line4X), (canvasDx + line4X), (canvasDy + line4X));
        ovalRectF = new RectF((-lineStartX - remainWidth), -pointRadius, (-lineStartX - remainWidth + pointRadius * 2), pointRadius);
    }

    private ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener(boolean isUpAnimator) {
        return animation -> {
            float f = (Float) animation.getAnimatedValue();
            startWidth = (int) ((canvasDx - lineStartX - remainWidth) * f);
            mRightArcRectF1.set((canvasDx - lineStartX - startWidth), (canvasDy - lineStartX - startWidth), (canvasDx + lineStartX), (canvasDy + lineStartX + startWidth));
            mRightArcRectF2.set((canvasDx - line2X - startWidth), (canvasDy - line2X - startWidth), (canvasDx + line2X), (canvasDy + line2X + startWidth));
            mRightArcRectF3.set((canvasDx - line3X - startWidth), (canvasDy - line3X - startWidth), (canvasDx + line3X), (canvasDy + line3X + startWidth));
            ovalRectF.set(-lineStartX - remainWidth - startWidth, -pointRadius, (-lineStartX - remainWidth + pointRadius * 2) - startWidth, pointRadius);
            //改变画笔的透明度

            int alpha = (int) (56f * f);
            int alpha2 = (int) (30f * f);
            if (isUpAnimator) {
                alpha = -alpha;
                alpha2 = -alpha2;
            }

            shortCalibrationLinePain.setAlpha(shortCalibrationLineAlpha + alpha2);
            longCalibrationLinePaint.setAlpha(longCalibrationLineAlpha + alpha);
            mRightArcPaint1.setAlpha(rightArcLineAlpha + alpha2);
            mRightArcPaint3.setAlpha(rightArcLine3Alpha + alpha2);
            mRightArcPaint2.setAlpha(rightArcLine2Alpha + alpha);

            invalidate();
        };
    }

    public void scrollToSize(int paramInt) {
        lastY = getMoveShortLineWidth(totalShortLineCount);
        overScroller.startScroll(0, Math.round(getMoveShortLineWidth(totalShortLineCount)), 0, -Math.round(getMoveShortLineWidth(paramInt)), 700);
        invalidate();
    }

    private void scrollTo() {
        float count = getScrollPositionCount();
        overScroller.startScroll(0, Math.round(getMoveShortLineWidth(totalShortLineCount)), 0, -Math.round(getMoveShortLineWidth(count)), 500);
        invalidate();
    }

    private void downStartAnimator() {
        if (isRunAnimator || isUpRunAnimator)
            return;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.addUpdateListener(getAnimatorUpdateListener(false));
        valueAnimator.addListener((Animator.AnimatorListener) new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator param1Animator) {
                super.onAnimationEnd(param1Animator);
                isRunAnimator = true;
                isUpRunAnimator = false;
            }

            public void onAnimationStart(Animator param1Animator) {
                super.onAnimationStart(param1Animator);
                isUpRunAnimator = false;
            }
        });
        valueAnimator.start();
    }

    private void upStartAnimator() {
        if (!isRunAnimator || isUpRunAnimator)
            return;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        valueAnimator.addUpdateListener(getAnimatorUpdateListener(true));
        valueAnimator.addListener((Animator.AnimatorListener) new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator param1Animator) {
                super.onAnimationEnd(param1Animator);
                isRunAnimator = false;
                isUpRunAnimator = false;
            }

            public void onAnimationStart(Animator param1Animator) {
                super.onAnimationStart(param1Animator);
                isUpRunAnimator = true;
            }
        });
        valueAnimator.start();
    }

    private float getScrollPositionCount() {
        return totalShortLineCount - (Math.round(totalShortLineCount / 15f) * 15);
    }

    private void playVibrator() {
        if (System.currentTimeMillis() < intervalTime + 100L)
            return;
        startVibrator();
        intervalTime = System.currentTimeMillis();
    }

    public void computeScroll() {
        if (overScroller.computeScrollOffset()) {
            updateView(overScroller.getCurrY() - lastY);
            lastY = overScroller.getCurrY();
            if (!overScroller.computeScrollOffset() && Math.round(getScrollPositionCount()) != 0) {
                lastY = getMoveShortLineWidth(totalShortLineCount);
                scrollTo();
                upStartAnimator();
            }
            return;
        }
        if (Math.round(getScrollPositionCount()) == 0 && !isDownEvent && isRunAnimator) {
            upStartAnimator();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        bh.a().b();
//        if (EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvasCalibration(canvas);
        canvasArcOrLine(canvas);
    }

    @Override
    protected void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
        setMeasuredDimension((int) (viewWidth), (int) (viewHeight));
    }

    // 外部控制转盘滑动距离
    public void onScrollEvent(int scrollX) {
        scrollToSize(scrollX);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        float differY;
        int yVelocity;
        float currentEventY = paramMotionEvent.getY();
        if (velocityTracker == null)
            velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(paramMotionEvent);
        switch (paramMotionEvent.getAction()) {
            default:
                return true;
            case MotionEvent.ACTION_DOWN:
                isDownEvent = true;
                moveTotalDy = 0f;
                if (!overScroller.isFinished())
                    overScroller.abortAnimation();
                lastY = currentEventY;
                downStartAnimator();
                return true;
            case MotionEvent.ACTION_MOVE:
                differY = lastY - currentEventY;
                moveTotalDy += differY;
                lastY = currentEventY;
                updateView(differY);
                return true;
            case MotionEvent.ACTION_UP:
                isDownEvent = false;
                if (Math.abs(moveTotalDy) < (ViewConfiguration.get(getContext()).getScaledTouchSlop() / 4f)) {
                    upStartAnimator();
                    return true;
                }
                velocityTracker.computeCurrentVelocity(1000, maxVelocity);
                yVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > minimumFlingVelocity) {
                    lastY = 0f;
                    fling(yVelocity);
                } else {
                    lastY = getMoveShortLineWidth(totalShortLineCount);
                    scrollTo();
                }
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                    return true;
                }
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        isDownEvent = false;
        if (!overScroller.isFinished())
            overScroller.abortAnimation();
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
            return true;
        }
        return false;
    }

    /**
     * 开始震动
     */
    @Suppress(names = "DEPRECATION")
    private void startVibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, 255));
        } else {
            vibrator.vibrate(30);
        }
    }

}
