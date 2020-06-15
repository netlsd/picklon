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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.Locale;

import id.co.picklon.utils.ViewUtils;


public class CircularProgress extends BaseProgressView {

    private static final int ANGLE_360 = 360;
    private RectF rectF;
    private float bottom;
    private float right;
    private float top;
    private float left;
    private float angle;

    private int min;
    private float angleX;
    private float angleY;
    private final float ANGLE_180 = 180f;
    private final int startAngle = -92;
    private float startX, startY;
    private int circlePadding;
    private int txtMarginY;
    private int circleSize;

    private Paint circlePaint;

    public CircularProgress(Context context) {
        super(context);
    }

    public CircularProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(Context ctx) {
        context = ctx;
        rectF = new RectF();
        setProgress(0);
        initDefaultValue();
        initBackgroundColor();
        initForegroundColor();
        initTextColor();
        initCircleColor();
    }

    private void initDefaultValue() {
        circleSize = ViewUtils.dpToPix(context, 15);
        txtMarginY = ViewUtils.dpToPix(context, 3);
        circlePadding = ViewUtils.dpToPix(context, 17);
    }

    @Override
    protected void initBackgroundColor() {
        super.initBackgroundColor();
    }

    @Override
    protected void initForegroundColor() {
        super.initForegroundColor();
    }

    @Override
    protected void initTextColor() {
        super.initTextColor();
    }

    private void initCircleColor() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(color);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        angle = ANGLE_360 * progress / MAX_PROGRESS;
        canvas.drawArc(rectF, startAngle, angle - ANGLE_360, false, backgroundPaint);
        if (progress > 1) {
            canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);
        }
        drawCircleText(canvas);
    }

    private void drawCircleText(Canvas canvas) {
        angleX = (float) ((angle + 1.5) * Math.PI / ANGLE_180);
        angleY = (float) ((angle + 2) * Math.PI / ANGLE_180);

        startX = (float) (min / 2 + rectF.width() / 2 * Math.sin(angleX));
        startY = (float) (min / 2 - rectF.width() / 2 * Math.cos(angleY));

        canvas.drawCircle(startX, startY, circleSize, circlePaint);
        canvas.drawText(String.format(Locale.ENGLISH, "%ds", (int) (progress / drawPerSecond)), startX, startY + txtMarginY,
                textPaint);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        min = setDimensions(widthMeasureSpec, heightMeasureSpec);

        left = 0 + strokeWidth / 2;
        top = 0 + strokeWidth / 2;
        right = min - strokeWidth / 2;
        bottom = min - strokeWidth / 2;
        rectF.set(left + PADDING + circlePadding, top + PADDING + circlePadding, right - PADDING - circlePadding, bottom
                - PADDING - circlePadding);
    }

    protected int setDimensions(int widthMeasureSpec, int heightMeasureSpec) {
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        final int smallerDimens = Math.min(width, height);

        setMeasuredDimension(smallerDimens, smallerDimens);
        return smallerDimens;
    }
}
