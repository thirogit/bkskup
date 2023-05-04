package com.bk.bkskup3.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import com.bk.bkskup3.R;

/**
 * Button with click-animation effect.
 */
public class MagicButton extends Button {
    int CLICK_FEEDBACK_COLOR;
    static final int CLICK_FEEDBACK_INTERVAL = 10;
    static final int CLICK_FEEDBACK_DURATION = 350;

    float mTextX;
    float mTextY;
    int mIconX, mIconY;
    long mAnimStart;
    Paint mFeedbackPaint;
    Drawable bm;


    public MagicButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MagicButton, 0, 0);
        bm = a.getDrawable(R.styleable.MagicButton_button_icon);
        init();

    }


    private void init() {
        Resources res = getResources();

        CLICK_FEEDBACK_COLOR = res.getColor(R.color.color_btn_magic_flame);
        mFeedbackPaint = new Paint();
        mFeedbackPaint.setStyle(Style.STROKE);
        mFeedbackPaint.setStrokeWidth(2);
        getPaint().setColor(res.getColor(R.color.color_btn_button_text));

        mAnimStart = -1;

    }


    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        measureText();
        measureIcon();
    }

    private void measureIcon() {
        if (bm != null) {
            mIconX = (getWidth() - bm.getIntrinsicWidth()) / 2;
            mIconY = (getHeight() - bm.getIntrinsicHeight()) / 2;
        }
    }

    private void measureText() {
        Paint paint = getPaint();
        mTextX = (getWidth() - paint.measureText(getText().toString())) / 2;
        mTextY = (getHeight() - paint.ascent() - paint.descent()) / 2;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        measureText();
    }

    private void drawMagicFlame(int duration, Canvas canvas) {
        int alpha = 255 - 255 * duration / CLICK_FEEDBACK_DURATION;
        int color = CLICK_FEEDBACK_COLOR | (alpha << 24);

        mFeedbackPaint.setColor(color);
        canvas.drawRect(1, 1, getWidth() - 1, getHeight() - 1, mFeedbackPaint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mAnimStart != -1) {
            int animDuration = (int) (System.currentTimeMillis() - mAnimStart);

            if (animDuration >= CLICK_FEEDBACK_DURATION) {
                mAnimStart = -1;
            } else {
                drawMagicFlame(animDuration, canvas);
                postInvalidateDelayed(CLICK_FEEDBACK_INTERVAL);
            }
        } else if (isPressed()) {
            drawMagicFlame(0, canvas);
        }


        if (bm != null) {

            bm.setBounds(mIconX, mIconY, mIconX+bm.getIntrinsicWidth(), mIconY+bm.getIntrinsicHeight());
            bm.draw(canvas);
        } else {
            CharSequence text = getText();
            canvas.drawText(text, 0, text.length(), mTextX, mTextY, getPaint());
        }


    }

    public void animateClickFeedback() {
        mAnimStart = System.currentTimeMillis();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isPressed()) {
                    animateClickFeedback();
                } else {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
                mAnimStart = -1;
                invalidate();
                break;
        }

        return result;
    }
}
