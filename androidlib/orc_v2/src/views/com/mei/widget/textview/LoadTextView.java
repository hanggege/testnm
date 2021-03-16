package com.mei.widget.textview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.mei.orc.R;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class LoadTextView extends AppCompatTextView {

    public static final String LOAD_TAG = "...\u200B";
    public static final String REPLACE_TAG = "   \u200B";
    private JumpingSpan dotOne;
    private JumpingSpan dotTwo;
    private JumpingSpan dotThree;

    private int jumpHeight;
    private boolean autoPlay = true;
    private boolean isPlaying;
    private boolean isHide;
    private int period = 1000;
    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private float textWidth;

    public LoadTextView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadTextView);
            period = typedArray.getInt(R.styleable.LoadTextView_period, 1000);
            autoPlay = typedArray.getBoolean(R.styleable.LoadTextView_autoplay, true);
            typedArray.recycle();
        } else {
            autoPlay = true;
            period = 1000;
        }
        jumpHeight = (int) (getTextSize() / 4);
        dotOne = new JumpingSpan();
        dotTwo = new JumpingSpan();
        dotThree = new JumpingSpan();

        textWidth = getPaint().measureText(".", 0, 1);

        ObjectAnimator dotOneJumpAnimator = createDotJumpAnimator(dotOne, 0);
        dotOneJumpAnimator.addUpdateListener(new InvalidateViewOnUpdate(this));

        mAnimatorSet.playTogether(dotOneJumpAnimator, createDotJumpAnimator(dotTwo,
                period / 6), createDotJumpAnimator(dotThree, period * 2 / 6));

        isPlaying = autoPlay;
        if (autoPlay && !isInEditMode()) {
            start();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (isPlaying()) {
            SpannableString spannable;
            int len = text.length();
            if (text.toString().endsWith(REPLACE_TAG)) {
                spannable = new SpannableString(text.toString().replace(REPLACE_TAG, LOAD_TAG));
                len = spannable.length() - LOAD_TAG.length();
            } else if (text.toString().endsWith(LOAD_TAG)) {
                spannable = new SpannableString(text);
                len = text.length() - LOAD_TAG.length();
            } else if (text.toString().contains(LOAD_TAG)) {
                spannable = new SpannableString(text.toString().replace(LOAD_TAG, "") + LOAD_TAG);
                len = spannable.length() - LOAD_TAG.length();
            } else {
                spannable = new SpannableString(text + LOAD_TAG);
            }
            spannable.setSpan(dotOne, len, len + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(dotTwo, len + 1, len + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(dotThree, len + 2, len + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            super.setText(spannable, type);
        } else {
            super.setText(text, type);
        }
    }

    public void start() {
        isPlaying = true;
        setText(getText());
        setAllAnimationsRepeatCount(ValueAnimator.INFINITE);
        mAnimatorSet.start();
    }

    private ObjectAnimator createDotJumpAnimator(JumpingSpan jumpingSpan, long delay) {
        ObjectAnimator jumpAnimator = ObjectAnimator.ofFloat(jumpingSpan, "translationY", 0, -jumpHeight);

        jumpAnimator.setEvaluator(new SinTypeEvaluator());

        jumpAnimator.setDuration(period);
        jumpAnimator.setStartDelay(delay);
        jumpAnimator.setRepeatCount(ValueAnimator.INFINITE);
        jumpAnimator.setRepeatMode(ValueAnimator.RESTART);
        return jumpAnimator;
    }

    public void stop() {
        isPlaying = false;
        setAllAnimationsRepeatCount(0);
    }

    private void setAllAnimationsRepeatCount(int repeatCount) {
        for (Animator animator : mAnimatorSet.getChildAnimations()) {
            if (animator instanceof ObjectAnimator) {
                ((ObjectAnimator) animator).setRepeatCount(repeatCount);
            }
        }
    }

    public void hide() {
        createDotHideAnimator(dotThree, 3).start();
        createDotHideAnimator(dotTwo, 2).start();

        ObjectAnimator oneMoveRightToLeft = createDotHideAnimator(dotOne, 1);
        oneMoveRightToLeft.addUpdateListener(new InvalidateViewOnUpdate(this));
        oneMoveRightToLeft.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setText(getText().toString().replace(LOAD_TAG, REPLACE_TAG));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        oneMoveRightToLeft.start();
        isHide = true;
    }

    public void show() {
        createDotShowAnimator(dotThree, 3).start();
        createDotShowAnimator(dotTwo, 2).start();

        ObjectAnimator dotOneMoveRightToLeft = createDotShowAnimator(dotOne, 1);
        dotOneMoveRightToLeft.addUpdateListener(new InvalidateViewOnUpdate(this));
        dotOneMoveRightToLeft.start();
        isHide = false;
    }

    private ObjectAnimator createDotHideAnimator(JumpingSpan span, float widthMultiplier) {
        return createDotHorizontalAnimator(span, 0, -textWidth * widthMultiplier);
    }

    private ObjectAnimator createDotShowAnimator(JumpingSpan span, int widthMultiplier) {
        return createDotHorizontalAnimator(span, -textWidth * widthMultiplier, 0);
    }

    private ObjectAnimator createDotHorizontalAnimator(JumpingSpan span, float from, float to) {
        ObjectAnimator dotThreeMoveRightToLeft = ObjectAnimator.ofFloat(span, "translationX", from, to);
        int showSpeed = 500;
        dotThreeMoveRightToLeft.setDuration(showSpeed);
        return dotThreeMoveRightToLeft;
    }

    @SuppressWarnings("unused")
    public void showAndPlay() {
        show();
        start();
    }

    @SuppressWarnings("unused")
    public void hideAndStop() {
        hide();
        stop();
    }

    public boolean isHide() {
        return isHide;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    @SuppressWarnings("unused")
    public void setPeriod(int period) {
        this.period = period;
    }


    private class JumpingSpan extends ReplacementSpan {

        private float translationX = 0;
        private float translationY = 0;

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
            return (int) paint.measureText(text, start, end);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            canvas.drawText(text, start, end, x + translationX, y + translationY, paint);
        }

        @SuppressWarnings("unused")
        @Keep
        public void setTranslationX(float translationX) {
            this.translationX = translationX;
        }

        @SuppressWarnings("unused")
        @Keep
        public void setTranslationY(float translationY) {
            this.translationY = translationY;
        }
    }

    private class InvalidateViewOnUpdate implements ValueAnimator.AnimatorUpdateListener {
        private final WeakReference<View> viewRef;

        InvalidateViewOnUpdate(View view) {
            this.viewRef = new WeakReference<>(view);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            final View view = viewRef.get();

            if (view == null) {
                return;
            }

            view.invalidate();
        }
    }

    private class SinTypeEvaluator implements TypeEvaluator<Number> {
        @Override
        public Number evaluate(float fraction, Number from, Number to) {
            return Math.max(0, Math.sin(fraction * Math.PI * 2)) * (to.floatValue() - from.floatValue());
        }
    }
}
