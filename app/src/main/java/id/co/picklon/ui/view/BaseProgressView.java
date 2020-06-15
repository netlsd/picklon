/*
 * Android-PercentProgressBar
 * Copyright (c) 2015  Natasa Misic
 *
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package id.co.picklon.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.TimeUnit;

import id.co.picklon.R;
import id.co.picklon.utils.ViewUtils;

abstract class BaseProgressView extends View {
    private static int duration = (int) TimeUnit.SECONDS.convert(2, TimeUnit.MINUTES);
    public static int INTERVAL = 200;
    protected static int drawPerSecond = (int) TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS) / INTERVAL;
    public static int MAX_PROGRESS = duration * drawPerSecond;

    protected float progress;

    protected Paint foregroundPaint, backgroundPaint, textPaint;
    protected float backgroundStrokeWidth, strokeWidth;
    protected int PADDING = 0;
    protected int color, backgroundColor, textColor, shadowColor;
    protected int textSize;
    protected int height, width;

    protected Context context;
    protected boolean isRoundEdge;
    protected boolean isShadowed;
    protected OnProgressTrackListener listener;

    public void setOnProgressTrackListener(OnProgressTrackListener listener) {
        this.listener = listener;
    }

    protected abstract void init(Context context);

    public BaseProgressView(Context context) {
        this(context, null);
    }

    public BaseProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultValue(context);
        initTypedArray(context, attrs);
        init(context);
    }

    void initDefaultValue(Context context) {
        backgroundStrokeWidth = strokeWidth = ViewUtils.dpToPix(context, 4);
        textSize = ViewUtils.dpToPix(context, 12);

        color = ContextCompat.getColor(context, R.color.colorPrimary);
        backgroundColor = ContextCompat.getColor(context, R.color.grey);
        shadowColor = backgroundColor;
        textColor = ContextCompat.getColor(context, R.color.white);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Progress, 0, 0);
        try {
            strokeWidth = typedArray.getDimension(
                    R.styleable.Progress_stroke_width, strokeWidth);
            backgroundStrokeWidth = typedArray.getDimension(
                    R.styleable.Progress_background_stroke_width,
                    backgroundStrokeWidth);
            color = typedArray.getInt(
                    R.styleable.Progress_progress_color, color);
            backgroundColor = typedArray.getInt(
                    R.styleable.Progress_background_color, backgroundColor);
            textColor = typedArray.getInt(
                    R.styleable.Progress_text_color, textColor);
            textSize = typedArray.getInt(
                    R.styleable.Progress_text_size, textSize);
        } finally {
            typedArray.recycle();
        }
        this.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    protected void initBackgroundColor() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        if (isRoundEdge) {
            backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        if (isShadowed) {
            backgroundPaint.setShadowLayer(3.0f, 0.0f, 2.0f, shadowColor);
        }
    }

    protected void initForegroundColor() {
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);
        if (isRoundEdge) {
            foregroundPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        if (isShadowed) {
            foregroundPaint.setShadowLayer(3.0f, 0.0f, 2.0f, shadowColor);
        }
    }

    protected void initTextColor() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(1f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        setProgressInView(progress);
    }

    private synchronized void setProgressInView(float progress) {
        this.progress = (progress <= MAX_PROGRESS) ? progress : MAX_PROGRESS;
        invalidate();
        trackProgressInView(progress);
    }

    private void trackProgressInView(float progress) {
        if (listener != null) {
            listener.onProgressUpdate(progress);
            if (progress >= MAX_PROGRESS) {
                listener.onProgressFinish();
            }
        }
    }

    public void resetProgress() {
        setProgress(0);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        init(context);
    }

    public int getProgressColor() {
        return color;
    }

    public void setProgressColor(int color) {
        this.color = color;
        init(context);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        init(context);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        init(context);
    }

    public void setProgressStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        init(context);
    }

    public void setBackgroundStrokeWidth(int strokeWidth) {
        this.backgroundStrokeWidth = strokeWidth;
        init(context);
    }

    public void setRoundEdge(boolean isRoundEdge) {
        this.isRoundEdge = isRoundEdge;
        init(context);
    }

    public void setShadow(boolean isShadowed) {
        this.isShadowed = isShadowed;
        init(context);
    }

    public interface OnProgressTrackListener {
        void onProgressFinish();

        void onProgressUpdate(float progress);
    }

}
