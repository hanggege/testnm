package com.mei.radio.tool;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import org.jetbrains.annotations.NotNull;

/**
 * Schedule a countdown until a time in the future, with
 * regular notifications on intervals along the way.
 * <p>
 * Example of showing a 30 second countdown in a text field:
 *
 * <pre class="prettyprint">
 * new CountdownTimer(30000, 1000) {
 *
 *     public void onTick(long millisUntilFinished) {
 *         mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
 *     }
 *
 *     public void onFinish() {
 *         mTextField.setText("done!");
 *     }
 *  }.start();
 * </pre>
 * <p>
 * The calls to {@link #onTick(long)} are synchronized to this object so that
 * one call to {@link #onTick(long)} won't ever occur before the previous
 * callback is complete.  This is only relevant when the implementation of
 * {@link #onTick(long)} takes an amount of time to execute that is significant
 * compared to the countdown interval.
 */
public abstract class AdvanceCountDownTimer {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    private long mPauseTime;

    private boolean mCancelled = false;

    private boolean mPaused = false;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public AdvanceCountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     * <p>
     * Do not call it from inside CountDownTimer threads
     */
    public final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    public synchronized final AdvanceCountDownTimer start() {
        mCancelled = false;
        mPaused = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    /**
     * Pause the countdown.
     */
    public long pause() {
        if (mPaused) return -1L;
        mPauseTime = mStopTimeInFuture - SystemClock.elapsedRealtime();
        mPaused = true;
        mHandler.removeMessages(MSG);
        return mPauseTime;
    }

    /**
     * Resume the countdown.
     */
    public long resume() {
        if (!mPaused || mCancelled) return -1L;
        mStopTimeInFuture = mPauseTime + SystemClock.elapsedRealtime();
        mPaused = false;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return mPauseTime;
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();


    private static final int MSG = 1;


    // handles counting down
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NotNull Message msg) {

            synchronized (AdvanceCountDownTimer.this) {
                if (mCancelled) {
                    return;
                }
                if (!mPaused) {
                    final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                    if (millisLeft <= 0) {
                        mCancelled = true;
                        onFinish();
                    } else {
                        long lastTickStart = SystemClock.elapsedRealtime();
                        onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart;
                        long delay;

                        if (millisLeft < mCountdownInterval) {
                            // just delay until done
                            delay = millisLeft - lastTickDuration;

                            // special case: user's onTick took more than interval to
                            // complete, trigger onFinish without delay
                            if (delay < 0) delay = 0;
                        } else {
                            delay = mCountdownInterval - lastTickDuration;

                            // special case: user's onTick took more than interval to
                            // complete, skip to next interval
                            while (delay < 0) delay += mCountdownInterval;
                        }

                        sendMessageDelayed(obtainMessage(MSG), delay);

                    }
                }
            }
        }
    };
}
