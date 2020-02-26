package com.example.customprogressbarproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.AttrRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CustomProgressView extends View {

    private Paint paint = new Paint();
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private float widgetHeight;
    private float radius;
    private float textPaddingBottom;
    private float textSize;
    private int mainColor;
    private int progressColor;

    private List<Label> labels = new ArrayList<>();
    private float pointTop = 0f;
    float progress = 0f;

    Drawable drawable;

    public CustomProgressView(@NonNull final Context context) {
        this(context, null);
    }

    public CustomProgressView(@NonNull final Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressView(@NonNull final Context context, @Nullable AttributeSet attrs,
                              @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressView, defStyleAttr, R.style.Progress_Bar_Widget);

        widgetHeight = typedArray.getDimension(R.styleable.CustomProgressView_customHeight, 0f);
        radius = typedArray.getDimension(R.styleable.CustomProgressView_radius, 0f);
        mainColor = typedArray.getColor(R.styleable.CustomProgressView_mainColor, 0);
        progressColor = typedArray.getColor(R.styleable.CustomProgressView_progressColor, 0);
        textPaddingBottom = typedArray.getDimension(R.styleable.CustomProgressView_textBottomPadding, 0f);
        textSize = typedArray.getDimension(R.styleable.CustomProgressView_textSize, 0f);
        typedArray.recycle();
    }

    public void makeHighInterestLabels(final List<TestRange> values) {
        labels.clear();
        for (int index = 0; index < values.size(); index++) {
            final int value = values.get(index).minValue()
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValueExact();

            final String text = getContext().getString(R.string.deposit_high_interest_first_value, value, values.get(index).highRate());
            final Layout staticLayout = makeLayout(text);
            labels.add(new Label(staticLayout));
        }
    }

    private Layout makeLayout(final CharSequence text) {
        textPaint.setTextSize(textSize);
        final double width = Math.ceil((double) Layout.getDesiredWidth(text, textPaint));
        return new StaticLayout(text, textPaint, (int) width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, true);
    }

    @SuppressWarnings("all")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Width
        int modifiedWidthMeasureSpec = 0;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        switch (widthSpecMode) {
            case MeasureSpec.AT_MOST: {
                int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
                int desireWidth = getDesireWidth();
                modifiedWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(widthSpecSize, desireWidth), MeasureSpec.EXACTLY);
                break;
            }
            case MeasureSpec.UNSPECIFIED: {
                int desireWidth = getDesireWidth();
                modifiedWidthMeasureSpec = MeasureSpec.makeMeasureSpec(desireWidth, MeasureSpec.EXACTLY);
                break;
            }
            case MeasureSpec.EXACTLY: {
                modifiedWidthMeasureSpec = widthMeasureSpec;
                break;
            }
        }

        //Height
        int modifiedHeightMeasureSpec = 0;
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST: {
                int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
                int desireHeight = getDesireHeight();
                modifiedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(heightSpecSize, desireHeight), MeasureSpec.EXACTLY);
                break;
            }
            case MeasureSpec.UNSPECIFIED: {
                int desireHeight = getDesireHeight();
                modifiedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(desireHeight, MeasureSpec.EXACTLY);
                break;
            }
            case MeasureSpec.EXACTLY: {
                modifiedHeightMeasureSpec = widthMeasureSpec;
                break;
            }
        }

        super.onMeasure(modifiedWidthMeasureSpec, modifiedHeightMeasureSpec);
    }

    @IntRange(from = 0)
    private int getDesireWidth() {
        final int left = getPaddingLeft();
        final int right = getPaddingRight();

        float maxTextWidth = 0f;
        for (int index = 0; index < labels.size(); index++) {
            maxTextWidth = Math.max(maxTextWidth, labels.get(index).minWidth);
        }

        int labelsCount = labels.size();
        return (int) (labelsCount * maxTextWidth + left + right);
    }

    @IntRange(from = 0)
    private int getDesireHeight() {
        float maxTextHeight = 0f;
        for (int index = 0; index < labels.size(); index++) {
            maxTextHeight = Math.max(maxTextHeight, labels.get(index).minHeight + textPaddingBottom);
        }
        pointTop = maxTextHeight;
        return (int) (maxTextHeight + widgetHeight);
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        float paddingLeft = (float) getPaddingLeft();
        float paddingRight = (float) getPaddingRight();
        float paddingTop = (float) getPaddingTop();

        float availableWidth = getWidth() - paddingLeft - paddingRight;

        float pointX = paddingLeft;

        //Draw progress bar
        paint.setColor(mainColor);
        canvas.drawRoundRect(new RectF(pointX, pointTop, pointX + availableWidth, pointTop + widgetHeight),
                radius, radius, paint);

        //Draw progress level
        paint.setColor(progressColor);

        int sectionsCount = labels.size() - 1;
        float sectionLength = availableWidth / sectionsCount;

        float progressLevel = sectionLength * progress;
        canvas.drawRoundRect(new RectF(pointX, pointTop, pointX + progressLevel, pointTop + widgetHeight),
                radius, radius, paint);

        //Draw text values and separators
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4f);

        int saveCount = canvas.save();
        canvas.translate(pointX, paddingTop);

        if (!labels.isEmpty()) {
            Label firstValue = labels.get(0);
            firstValue.draw(canvas, pointX, paddingTop, false);

            Label lastValue = labels.get(labels.size() - 1);
            lastValue.draw(canvas, pointX + availableWidth - lastValue.minWidth, paddingTop, false);

            if (drawable != null) {
                final Image icon = new Image(drawable);

                float previousPoint = pointX;
                final float currentWidth = (float) getWidth();
                while (previousPoint < currentWidth) {
                    final float nextPoint = previousPoint + sectionLength;
                    if (pointX + progressLevel >= nextPoint) {
                        final float point = (nextPoint + previousPoint) / 2;
                        icon.draw(canvas, point, paddingTop + 10f);
                    }
                    previousPoint = nextPoint;
                }
            }

            for (int index = 1; index < labels.size() - 1; index++) {
                pointX += sectionLength;
                Label value = labels.get(index);
                value.draw(canvas, pointX, paddingTop, true);
                canvas.drawLine(pointX, pointTop, pointX, pointTop + widgetHeight, paint);
            }
        }

        canvas.restoreToCount(saveCount);

        super.draw(canvas);
    }

    private class Label {

        private final Layout mStaticLayout;
        private float minWidth;
        private float minHeight;

        Label(@NonNull final Layout staticLayout) {
            mStaticLayout = staticLayout;
            minWidth = (float) mStaticLayout.getWidth();
            minHeight = (float) mStaticLayout.getHeight();
        }

        void draw(Canvas canvas, float left, float top, boolean isCenter) {
            int saveCount = canvas.save();

            if (isCenter) {
                canvas.clipRect(left - minWidth / 2, top, left + minWidth / 2, top + minHeight);
                canvas.translate(left - minWidth / 2, top);
            } else {
                canvas.clipRect(left, top, left + minWidth, top + minHeight);
                canvas.translate(left, top);
            }
            mStaticLayout.draw(canvas);

            canvas.restoreToCount(saveCount);
        }
    }

    private class Image {
        private Drawable mDrawable;
        private float mDrawableWidth;
        private float mDrawableHeight;

        Image(final Drawable drawable) {
            mDrawable = drawable;
            mDrawableWidth = (float) mDrawable.getIntrinsicWidth() / 2;
            mDrawableHeight = (float) mDrawable.getIntrinsicHeight() / 2;
        }

        void draw(Canvas canvas, float left, float top) {
            int saveCount = canvas.save();
            canvas.clipRect(left - mDrawableWidth / 2, top, left + mDrawableWidth / 2, top + mDrawableHeight);
            canvas.translate(left - mDrawableWidth / 2, top);
            mDrawable.setBounds(0, 0, (int) mDrawableWidth, (int) mDrawableHeight);
            mDrawable.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

}
