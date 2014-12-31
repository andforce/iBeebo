
package org.zarroboogs.weibo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * User: qii Date: 12-12-23
 */
public class CircleProgressView extends View {

    private Paint mPaint = new Paint();
    private Paint mBGPaint = new Paint();

    private int progress = 0;

    private int max = 100;

    private ValueAnimator valueAnimator;

    private boolean isInitValue = true;

    RectF mProgressRectF = new RectF();
    RectF bgRectF = new RectF();

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Style.STROKE);
        mPaint.setAntiAlias(true);

        mBGPaint.setStrokeWidth(8);
        mBGPaint.setAntiAlias(true);
        mBGPaint.setColor(0x99000000);
        mBGPaint.setStyle(Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int h = Math.min(width, height);
        mProgressRectF.set((width - h) / 2 + 10, (height - h) / 2 + 10, h + (width - h) / 2 - 10, h + (height - h) / 2 - 10);
        bgRectF.set((width - h) / 2 + 5, (height - h) / 2 + 5, h + (width - h) / 2 - 5, h + (height - h) / 2 - 5);

        canvas.drawArc(bgRectF, 180, 360, true, mBGPaint);

        mPaint.setColor(0xFFFFFFFF);
        canvas.drawArc(mProgressRectF, 180, getProgress(), false, mPaint);
    }

    private int getProgress() {
        return 360 * progress / max;
    }

    public void setMax(int number) {
        this.max = number;
        invalidate();
    }

    public void setProgress(int progress) {
        if (progress == 0) {
            invalidate();
            return;
        }

        if (progress <= this.progress) {
            this.progress = progress;
            invalidate();
            return;
        }

        if (isInitValue) {
            isInitValue = false;
            this.progress = progress;
            invalidate();
            return;
        }

        int start = this.progress;

        if (valueAnimator != null && valueAnimator.isRunning()) {
            start = (Integer) valueAnimator.getAnimatedValue();
            valueAnimator.cancel();
        }

        valueAnimator = ValueAnimator.ofInt(start, progress);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                CircleProgressView.this.progress = value;
                // postInvalidateOnAnimation();
                postInvalidate();
            }
        });
        valueAnimator.start();

    }

    public void executeRunnableAfterAnimationFinish(final Runnable runnable) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }
}
