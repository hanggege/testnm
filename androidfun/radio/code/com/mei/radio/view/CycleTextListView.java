package com.mei.radio.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by lenna on 2020/9/22
 * 文本滚动选择器
 */
public class CycleTextListView extends View {
    private static final String[] mTitles = new String[]{"聆听自然", "就想听歌", "随便听听", "甜蜜陪伴", "宝宝睡觉"};
    private static final int mTextColor = Color.parseColor("#ffffff");
    private final Context mContext;
    private int measuredHeight;
    private int measuredWidth;
    //选中垂直间距
    private int verticalSpacing;
    //处于中间的间距
    private int centerVerticalSpacing;
    //最边上的两个间距
    private int minVerticalSpacing;
    //获取文字基准线矩形
    private Rect mDefRect = new Rect();
    private VelocityTracker velocityTracker;

    private int maxVelocity;
    private int mMinimumFlingVelocity;
    private int flingMinY;
    private int flingMaxY;
    private OverScroller overScroller;
    private float lastY = 0f;
    private float totalY = 0f;
    private int textIndex = -1;
    private String mFirstText = "";
    private String mOnSelectedText = "";
    private boolean mIsDownEvent = false;
    private OnTextSelectedListener mOnTextSelectedListener;
    private onTextClickScrollListener mOnTextClickScrollListener;

    private Paint mTextPaint;
    private int viewWidth;
    private int viewHeight;
    private int margin;

    private RectF[] textRectFs = new RectF[mTitles.length];
    private int[] textSizes;
    private int[] textPaintAlphas;
    private RectF mSelectRectF;
    private List<RectF> mRectFList;
    private List<Float> mTextSizeList;
    private List<Integer> mTextAlphaList;
    private List<String> mTextList;

    public CycleTextListView(Context context) {
        this(context, null);
    }

    public CycleTextListView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public CycleTextListView(Context context, AttributeSet attr, int def) {
        super(context, attr, def);
        mContext = context;
        initSize();
        initRectF();
        initData();
    }

    private void initSize() {
        //初始化惯性距离
        flingMinY = (int) (dip2px(51.5F) * -24);
        flingMaxY = (int) (dip2px(51.5F) * 24);

        measuredHeight = (int) dip2px(220f);
        measuredWidth = (int) dip2px(136f);
        verticalSpacing = (int) dip2px(50f);
        centerVerticalSpacing = (int) dip2px(29f);
        minVerticalSpacing = (int) dip2px(20f);


        viewWidth = measuredWidth;
        viewHeight = measuredHeight;

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        mTextPaint.setAntiAlias(true);
        overScroller = new OverScroller(getContext());
        velocityTracker = VelocityTracker.obtain();
        maxVelocity = 5000;
        mMinimumFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        margin = (int) dip2px(12f);

        //字体大小初始化
        textSizes = new int[]{(int) dip2px(14f), (int) dip2px(20f), (int) dip2px(34f), (int) dip2px(20f), (int) dip2px(14f)};
        //列表画笔的透明度初始化
        textPaintAlphas = new int[]{51, 128, 255, 128, 51};
    }

    public float dip2px(float paramFloat) {
        return (mContext.getResources().getDisplayMetrics()).density * paramFloat;
    }


    private void scrollToOffset(float offsetY) {
        RectF rectF = getOffsetRectF(offsetY);
        float scrollOffsetY = getScrollOffset(offsetY);
        if (offsetY > 0f) {
            float resultOffsetY;
            if (scrollOffsetY > 1f) {
                float centerY = mSelectRectF.centerY() - rectF.centerY();
                resultOffsetY = centerY;
                scrollOffsetY = 1f;
                centerY = offsetY - centerY;
                offsetY = resultOffsetY;
                resultOffsetY = centerY;
            } else {
                resultOffsetY = 0f;
            }
            if ((mRectFList.get(mRectFList.size() - 1)).bottom >= viewHeight) {
                RectF rectF1 = mRectFList.remove(mRectFList.size() - 1);
                rectF1.offset(0f, -viewHeight);
                mRectFList.add(0, rectF1);
                mTextAlphaList.add(0, mTextAlphaList.remove(mTextAlphaList.size() - 1));
                mTextSizeList.add(0, mTextSizeList.remove(mTextSizeList.size() - 1));
                mTextList.remove(mTextList.size() - 1);
                mTextList.add(0, mTextList.get(mTextList.size() - 1));
                textIndex++;
            }
            float targetOffsetY = textRectFs[1].height() + (textRectFs[2].height() - textRectFs[1].height()) * scrollOffsetY;
            offsetY = rectF.centerY() + offsetY;
            rectF.top = offsetY - targetOffsetY / 2f;
            rectF.bottom = targetOffsetY / 2f + offsetY;
            mTextAlphaList.set(textIndex, (int) (textPaintAlphas[1] + (textPaintAlphas[2] - textPaintAlphas[1]) * scrollOffsetY));
            mTextSizeList.set(textIndex, textSizes[1] + (textSizes[2] - textSizes[1]) * scrollOffsetY);
            if (textIndex - 1 >= 0) {
                rectF = mRectFList.get(textIndex - 1);
                int i = (int) Math.ceil((textRectFs[0].centerY() + (textRectFs[1].centerY() - textRectFs[0].centerY()) * scrollOffsetY));
                int j = (int) Math.ceil((textRectFs[0].height() + (textRectFs[1].height() - textRectFs[0].height()) * scrollOffsetY));
                rectF.top = (int) Math.floor((i - j / 2f));
                rectF.bottom = (int) Math.ceil((i + j / 2f));
                mTextAlphaList.set(textIndex - 1, (int) (textPaintAlphas[0] + (textPaintAlphas[1] - textPaintAlphas[0]) * scrollOffsetY));
                mTextSizeList.set(textIndex - 1, textSizes[0] + (textSizes[1] - textSizes[0]) * scrollOffsetY);
            }
            if (textIndex - 2 >= 0) {
                rectF = mRectFList.get(textIndex - 2);
                int i = (int) Math.ceil((0f + (textRectFs[0].centerY() - 0f) * scrollOffsetY));
                int j = (int) Math.ceil((0f + (textRectFs[0].centerY() - 0f) * scrollOffsetY));
                rectF.top = (int) Math.floor((i - j / 2f));
                rectF.bottom = (int) Math.ceil((i + j / 2f));
                mTextAlphaList.set(textIndex - 2, (int) (textPaintAlphas[0] * scrollOffsetY));
                mTextSizeList.set(textIndex - 2, textSizes[0] * scrollOffsetY);
            }
            if (textIndex + 1 <= mRectFList.size() - 1) {
                rectF = mRectFList.get(textIndex + 1);
                int i = (int) Math.ceil((textRectFs[2].centerY() + (textRectFs[3].centerY() - textRectFs[2].centerY()) * scrollOffsetY));
                int j = (int) Math.ceil((textRectFs[2].height() + (textRectFs[3].height() - textRectFs[2].height()) * scrollOffsetY));
                rectF.top = (int) Math.floor((i - j / 2f));
                rectF.bottom = (int) Math.ceil((i + j / 2f));
                mTextAlphaList.set(textIndex + 1, (int) (textPaintAlphas[2] + (textPaintAlphas[3] - textPaintAlphas[2]) * scrollOffsetY));
                mTextSizeList.set(textIndex + 1, textSizes[2] + (textSizes[3] - textSizes[2]) * scrollOffsetY);
            }
            if (textIndex + 2 <= mRectFList.size() - 1) {
                rectF = mRectFList.get(textIndex + 2);
                int i = (int) Math.ceil((textRectFs[3].centerY() + (textRectFs[4].centerY() - textRectFs[3].centerY()) * scrollOffsetY));
                int j = (int) Math.ceil((textRectFs[3].height() + (textRectFs[4].height() - textRectFs[3].height()) * scrollOffsetY));
                rectF.top = (int) Math.floor((i - j / 2f));
                rectF.bottom = (int) Math.ceil((i + j / 2f));
                mTextAlphaList.set(textIndex + 2, (int) (textPaintAlphas[3] + (textPaintAlphas[4] - textPaintAlphas[3]) * scrollOffsetY));
                mTextSizeList.set(textIndex + 2, textSizes[3] + (textSizes[4] - textSizes[3]) * scrollOffsetY);
            }
            if (textIndex + 3 <= mRectFList.size() - 1) {
                rectF = mRectFList.get(textIndex + 3);
                int i = (int) Math.ceil((textRectFs[4].centerY() + (viewHeight - textRectFs[4].centerY()) * scrollOffsetY));
                int j = (int) Math.ceil((textRectFs[4].height() + (0f - textRectFs[4].height()) * scrollOffsetY));
                rectF.top = (int) Math.floor((i - j / 2f));
                rectF.bottom = (int) Math.ceil((i + j / 2f));
                mTextAlphaList.set(textIndex + 3, (int) (textPaintAlphas[4] + (-textPaintAlphas[4]) * scrollOffsetY));
                List<Float> list = mTextSizeList;
                i = textIndex;
                offsetY = textSizes[4];
                list.set(i + 3, scrollOffsetY * (-textSizes[4]) + offsetY);
            }
            offsetY = resultOffsetY;
            if (((RectF) mRectFList.get(mRectFList.size() - 1)).bottom >= viewHeight) {
                rectF = mRectFList.remove(mRectFList.size() - 1);
                rectF.offset(0f, -viewHeight);
                mRectFList.add(0, rectF);
                mTextAlphaList.add(0, mTextAlphaList.remove(mTextAlphaList.size() - 1));
                mTextSizeList.add(0, mTextSizeList.remove(mTextSizeList.size() - 1));
                mTextList.remove(mTextList.size() - 1);
                mTextList.add(0, mTextList.get(mTextList.size() - 1));
                offsetY = resultOffsetY;
            }
        } else {

            float resultOffsetY;
            if (scrollOffsetY > 1f) {
                float centerY = mSelectRectF.centerY() - rectF.centerY();
                resultOffsetY = centerY;
                scrollOffsetY = 1f;
                centerY = offsetY - centerY;
                offsetY = resultOffsetY;
                resultOffsetY = centerY;
            } else {
                resultOffsetY = 0f;
            }
            float targetOffsetY = textRectFs[3].height() + (mSelectRectF.height() - textRectFs[3].height()) * scrollOffsetY;
            offsetY = rectF.centerY() + offsetY;
            rectF.top = offsetY - targetOffsetY / 2f;
            rectF.bottom = targetOffsetY / 2f + offsetY;
            mTextAlphaList.set(textIndex, (int) (textPaintAlphas[3] + (textPaintAlphas[2] - textPaintAlphas[3]) * scrollOffsetY));
            mTextSizeList.set(textIndex, textSizes[3] + (textSizes[2] - textSizes[3]) * scrollOffsetY);
            if (textIndex - 1 >= 0) {
                rectF = mRectFList.get(textIndex - 1);
                offsetY = textRectFs[2].centerY() + (textRectFs[1].centerY() - textRectFs[2].centerY()) * scrollOffsetY;
                targetOffsetY = textRectFs[2].height() + (textRectFs[1].height() - textRectFs[2].height()) * scrollOffsetY;
                rectF.top = (int) Math.floor((offsetY - targetOffsetY / 2f));
                rectF.bottom = (int) Math.ceil((offsetY + targetOffsetY / 2f));
                mTextAlphaList.set(textIndex - 1, (int) (textPaintAlphas[2] + (textPaintAlphas[1] - textPaintAlphas[2]) * scrollOffsetY));
                mTextSizeList.set(textIndex - 1, textSizes[2] + (textSizes[1] - textSizes[2]) * scrollOffsetY);
            }
            if (textIndex - 2 >= 0) {
                rectF = mRectFList.get(textIndex - 2);
                offsetY = textRectFs[1].centerY() + (textRectFs[0].centerY() - textRectFs[1].centerY()) * scrollOffsetY;
                targetOffsetY = textRectFs[1].height() + (textRectFs[0].height() - textRectFs[1].height()) * scrollOffsetY;
                rectF.top = (int) Math.floor((offsetY - targetOffsetY / 2f));
                rectF.bottom = (int) Math.ceil((offsetY + targetOffsetY / 2f));
                mTextAlphaList.set(textIndex - 2, (int) (textPaintAlphas[1] + (textPaintAlphas[0] - textPaintAlphas[1]) * scrollOffsetY));
                mTextSizeList.set(textIndex - 2, textSizes[1] + (textSizes[0] - textSizes[1]) * scrollOffsetY);
            }
            if (textIndex - 3 >= 0) {
                rectF = mRectFList.get(textIndex - 3);
                offsetY = textRectFs[0].centerY() + (0f - textRectFs[0].centerY()) * scrollOffsetY;
                targetOffsetY = textRectFs[0].height() + (0f - textRectFs[0].height()) * scrollOffsetY;
                rectF.top = (int) Math.floor((offsetY - targetOffsetY / 2f));
                rectF.bottom = (int) Math.ceil((offsetY + targetOffsetY / 2f));
                mTextAlphaList.set(textIndex - 3, (int) (textPaintAlphas[0] + (-textPaintAlphas[0]) * scrollOffsetY));
                mTextSizeList.set(textIndex - 3, textSizes[0] + (-textSizes[0]) * scrollOffsetY);
            }
            if (textIndex + 1 <= mRectFList.size() - 1) {
                rectF = mRectFList.get(textIndex + 1);
                offsetY = textRectFs[4].centerY() + (textRectFs[3].centerY() - textRectFs[4].centerY()) * scrollOffsetY;
                targetOffsetY = textRectFs[4].height() + (textRectFs[3].height() - textRectFs[4].height()) * scrollOffsetY;
                rectF.top = (int) Math.floor((offsetY - targetOffsetY / 2f));
                rectF.bottom = (int) Math.ceil((offsetY + targetOffsetY / 2f));
                mTextAlphaList.set(textIndex + 1, (int) (textPaintAlphas[4] + (textPaintAlphas[3] - textPaintAlphas[4]) * scrollOffsetY));
                mTextSizeList.set(textIndex + 1, textSizes[4] + (textSizes[3] - textSizes[4]) * scrollOffsetY);
            }
            if (textIndex + 2 <= mRectFList.size() - 1) {
                rectF = mRectFList.get(textIndex + 2);
                offsetY = viewHeight + (textRectFs[4].centerY() - viewHeight) * scrollOffsetY;
                targetOffsetY = 0f + (textRectFs[4].height() - 0f) * scrollOffsetY;
                rectF.top = (int) Math.floor((offsetY - targetOffsetY / 2f));
                rectF.bottom = (int) Math.ceil((offsetY + targetOffsetY / 2f));
                mTextAlphaList.set(textIndex + 2, (int) (textPaintAlphas[4] * scrollOffsetY));
                mTextSizeList.set(textIndex + 2, scrollOffsetY * textSizes[4]);
            }
            offsetY = resultOffsetY;
            if (((RectF) mRectFList.get(0)).top <= 0f) {
                rectF = mRectFList.remove(0);
                rectF.offset(0f, viewHeight);
                mRectFList.add(rectF);
                mTextList.remove(0);
                mTextList.add(mTextList.get(0));
                mTextAlphaList.add(mTextAlphaList.remove(0));
                mTextSizeList.add(mTextSizeList.remove(0));
                offsetY = resultOffsetY;
            }
        }
        invalidate();
        if (offsetY != 0f)
            scrollToOffset(offsetY);
    }

    private void selectRectFIndex(int paramInt) {
        lastY = getSelectRectF().centerY();
        int i = mRectFList.indexOf(getSelectRectF());
        overScroller.startScroll(0, Math.round(lastY), 0, Math.round((i - paramInt) * (textRectFs[2].centerY() - textRectFs[1].centerY())), 500);
        invalidate();
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < mRectFList.size(); i++) {
            mTextPaint.setTextSize(mTextSizeList.get(i));
            mTextPaint.setAlpha(mTextAlphaList.get(i));
            drawText(canvas, mRectFList.get(i), mTextList.get(i));
        }
    }

    private void drawText(Canvas canvas, RectF rectf, String content) {
        mTextPaint.getTextBounds(content, 0, content.length(), mDefRect);
        //基准线
        float centerY = rectf.centerY() - mDefRect.centerY();
        canvas.drawText(content, 0f, centerY, mTextPaint);
    }

    private void scrollRectF(MotionEvent event) {
        for (RectF rectF : mRectFList) {
            if (rectF.contains(Math.round(event.getX()), Math.round(event.getY()))) {
                selectRectFIndex(mRectFList.indexOf(rectF));
                if (mOnTextClickScrollListener != null) {
                    int offset = (getSelectRectFIndex() - mRectFList.indexOf(rectF)) * 15;
                    mOnTextClickScrollListener.onClickScrollToOffset(offset);
                }
            }
        }
    }

    private float getScrollOffset(float paramFloat) {
        RectF rectF = getOffsetRectF(paramFloat);
        return (paramFloat > 0f) ? ((rectF.centerY() + paramFloat - textRectFs[1].centerY()) / (mSelectRectF.centerY() - textRectFs[1].centerY())) : ((textRectFs[3].centerY() - rectF.centerY() - paramFloat) / (textRectFs[3].centerY() - mSelectRectF.centerY()));
    }

    private void initRectF() {
        //画文本矩形初始化
        //被选中的位置，处于view的最中央
        textRectFs[2] = new RectF(0f, viewHeight / 2f - verticalSpacing / 2f, viewWidth, viewHeight / 2f + verticalSpacing / 2f);
        //处于选中和最边上的矩形
        textRectFs[1] = new RectF(0f, (textRectFs[2]).top - margin - centerVerticalSpacing, viewWidth, (textRectFs[2]).top - margin);
        textRectFs[3] = new RectF(0f, (textRectFs[2]).bottom + margin, viewWidth, (textRectFs[2]).bottom + margin + centerVerticalSpacing);
        //最边上的两个
        textRectFs[0] = new RectF(0f, (textRectFs[1]).top - margin - minVerticalSpacing, viewWidth, (textRectFs[1]).top - margin);
        textRectFs[4] = new RectF(0f, (textRectFs[3]).bottom + margin, viewWidth, (textRectFs[3]).bottom + margin + minVerticalSpacing);
        mSelectRectF = textRectFs[2];
    }

    private void fling(int paramInt) {
        overScroller.fling(0, 0, 0, paramInt, 0, 0, flingMinY, flingMaxY);
        invalidate();
    }

    /**
     * 获取到应该滚动到那一个RectF
     */
    private RectF getOffsetRectF(float paramFloat) {
        RectF rectF1 = null;
        RectF rectF2 = null;
        if (paramFloat > 0f) {
            int i = 0;
            rectF1 = rectF2;
            while (true) {
                rectF2 = rectF1;
                if (i < mRectFList.size()) {
                    if (((RectF) mRectFList.get(i)).centerY() < mSelectRectF.centerY()) {
                        rectF1 = mRectFList.get(i);
                        textIndex = i;
                    }
                    i++;
                    continue;
                }
                break;
            }
        } else {
            for (int i = 0; ; i++) {
                rectF2 = rectF1;
                if (i < mRectFList.size()) {
                    if (((RectF) mRectFList.get(i)).centerY() > mSelectRectF.centerY()) {
                        textIndex = i;
                        rectF2 = mRectFList.get(i);
                        return rectF2;
                    }
                } else {
                    return rectF2;
                }
            }
        }
        return rectF2;
    }


    private void initData() {
        mRectFList = new ArrayList(mTitles.length + 1);
        mTextSizeList = new ArrayList(mTitles.length + 1);
        mTextAlphaList = new ArrayList(mTitles.length + 1);
        mTextList = new ArrayList(mTitles.length + 1);
        for (int i = 0; i < mTitles.length; i++) {
            mTextList.add(mTitles[i]);
            mTextAlphaList.add(textPaintAlphas[i]);
            mRectFList.add(new RectF(textRectFs[i]));
            mTextSizeList.add((float) textSizes[i]);
        }
        mTextList.add(0, mTitles[mTitles.length - 1]);
        mTextAlphaList.add(0, 0);
        mRectFList.add(0, new RectF(0f, 0f, viewWidth, 0f));
        mTextSizeList.add(0, 0f);
        mFirstText = mTitles[0];
    }

    private void scrollSelectText() {
        RectF rectF = getSelectRectF();
        overScroller.startScroll(0, Math.round(rectF.centerY()), 0, Math.round(mSelectRectF.centerY() - rectF.centerY()), 500);
        invalidate();
    }

    private RectF getSelectRectF() {
        float maxValue = Integer.MAX_VALUE;
        RectF rectF = null;
        for (RectF rectF1 : mRectFList) {
            float toSelectRectFCenterY = Math.abs(rectF1.centerY() - mSelectRectF.centerY());
            if (toSelectRectFCenterY < maxValue) {
                maxValue = toSelectRectFCenterY;
                rectF = rectF1;
            }
        }
        return rectF;
    }

    private int getSelectRectFIndex() {
        float maxValue = Integer.MAX_VALUE;
        RectF rectF = null;
        for (RectF rectF1 : mRectFList) {
            float toSelectRectFCenterY = Math.abs(rectF1.centerY() - mSelectRectF.centerY());
            if (toSelectRectFCenterY < maxValue) {
                maxValue = toSelectRectFCenterY;
                rectF = rectF1;
            }
        }
        return mRectFList.indexOf(rectF);
    }

    private String getTextStr() {
        int selectIndex = 0;
        float maxValue = Integer.MAX_VALUE;
        int index = 0;
        while (index < mRectFList.size()) {
            float toSelectRectFCenterY = Math.abs(((RectF) mRectFList.get(index)).centerY() - mSelectRectF.centerY());
            float value = maxValue;
            if (toSelectRectFCenterY < maxValue) {
                selectIndex = index;
                value = toSelectRectFCenterY;
            }
            index++;
            maxValue = value;
        }
        return mTextList.get(selectIndex);
    }

    @Override
    public void computeScroll() {
        if (overScroller.computeScrollOffset()) {
            scrollToOffset(overScroller.getCurrY() - lastY);
            lastY = overScroller.getCurrY();
            if (!overScroller.computeScrollOffset() && !getSelectRectF().equals(mSelectRectF)) {
                lastY = getSelectRectF().centerY();
                scrollSelectText();
                if (mOnTextSelectedListener != null && !mOnSelectedText.equals(getTextStr())) {
                    mOnTextSelectedListener.onTextSelected(getTextStr());
                    mOnSelectedText = getTextStr();
                }
            }
            return;
        }
        if (mOnTextSelectedListener != null && getSelectRectF().equals(mSelectRectF) && !mOnSelectedText.equals(getTextStr()) && !mIsDownEvent) {
            mOnTextSelectedListener.onTextSelected(getTextStr());
            mOnSelectedText = getTextStr();
        }
    }

    @Override
    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        drawText(paramCanvas);
    }

    @Override
    protected void onMeasure(int paramInt1, int paramInt2) {
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        float poorY;
        int scrollY;
        float currentY = paramMotionEvent.getY();
        if (velocityTracker == null)
            velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(paramMotionEvent);
        switch (paramMotionEvent.getAction()) {
            default:
                return true;
            case MotionEvent.ACTION_DOWN:
                mIsDownEvent = true;
                totalY = 0f;
                if (!overScroller.isFinished())
                    overScroller.abortAnimation();
                lastY = currentY;
                return true;
            case MotionEvent.ACTION_MOVE:
                poorY = lastY - currentY;
                totalY += poorY;
                lastY = currentY;
                scrollToOffset(-poorY);
                return true;
            case MotionEvent.ACTION_UP:
                mIsDownEvent = false;
                int minY = ViewConfiguration.get(getContext()).getScaledTouchSlop() / 4;
                if (Math.abs(totalY) < minY) {
                    scrollRectF(paramMotionEvent);
                    return true;
                }
                velocityTracker.computeCurrentVelocity(1000, maxVelocity);
                scrollY = Math.round(velocityTracker.getYVelocity());
                if (Math.abs(scrollY) > mMinimumFlingVelocity) {
                    lastY = 0f;
                    fling(scrollY);
                } else {
                    lastY = getSelectRectF().centerY();
                    scrollSelectText();
                }
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                    return true;
                }
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        mIsDownEvent = false;
        if (!overScroller.isFinished())
            overScroller.abortAnimation();
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
            return true;
        }
        return false;
    }

    public void setCurrentTitle(String paramString) {
        if (mTextList.lastIndexOf(paramString) != -1)
            selectRectFIndex(mTextList.lastIndexOf(paramString));
    }

    public void setTextSelectedListener(OnTextSelectedListener listener) {
        mOnTextSelectedListener = listener;
    }

    public void setTextClickScrollListener(onTextClickScrollListener listener) {
        mOnTextClickScrollListener = listener;
    }

    public void setTitles(List<String> paramList) {
        if (paramList != null && paramList.size() != 0) {
            mFirstText = paramList.get(0);
            mTextList.clear();
            mTextList.addAll(paramList);
            mTextList.add(0, paramList.get(paramList.size() - 1));
            invalidate();
        }
    }

    private List<String> tempList = new ArrayList<>();

    public void setTitleWithLocation(List<String> rawList) {
        if (rawList != null && rawList.size() != 0) {
            tempList.clear();
            tempList.addAll(rawList);
            adjustLocation();
            tempList.add(0, tempList.get(tempList.size() - 1));
            mTextList.clear();
            mTextList.addAll(tempList);
            invalidate();
        }
    }

    public void setTitleWithLocation(List<String> rawList, String nextFocus) {
        if (rawList != null && rawList.size() != 0 && rawList.contains(nextFocus)) {
            tempList.clear();
            tempList.addAll(rawList);
            mOnSelectedText = nextFocus;
            adjustLocation();
            tempList.add(0, tempList.get(tempList.size() - 1));
            mTextList.clear();
            mTextList.addAll(tempList);
            invalidate();
            mOnTextSelectedListener.onTextSelected(nextFocus);
        }
    }

    private void adjustLocation() {
        if (mOnSelectedText.equals(tempList.get(1))) {
            return;
        }
        if (mOnSelectedText.equals(tempList.get(0))) {
            tempList.add(0, tempList.remove(tempList.size() - 1));
            return;
        }
        int index = tempList.indexOf(mOnSelectedText);
        for (; index > 1; index--) {
            tempList.add(tempList.remove(0));
        }
    }

    public interface OnTextSelectedListener {
        void onTextSelected(String param1String);
    }

    public interface onTextClickScrollListener {
        void onClickScrollToOffset(int toOffset);
    }

}
