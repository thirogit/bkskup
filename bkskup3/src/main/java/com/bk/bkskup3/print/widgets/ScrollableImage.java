package com.bk.bkskup3.print.widgets;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 10/9/2014
 * Time: 10:41 PM
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ScrollableImage extends View {
    private Bitmap bmLargeImage; // bitmap large enough to be scrolled
    private Rect displayRect = null; // rect we display to
    private Rect scrollRect = null; // rect we scroll over our bitmap with
    private int scrollRectX = 0; // current left location of scroll rect
    private int scrollRectY = 0; // current top location of scroll rect
    private float scrollByX = 0; // x amount to scroll by
    private float scrollByY = 0; // y amount to scroll by

    private int width, height;

    private Paint background;

    private float mLastMotionY = 0.0f;

    public ScrollableImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setSize(w, h);
    }

    private void setSize(int width, int height) {
        background = new Paint();
        background.setColor(Color.WHITE);
        this.width = width;
        this.height = height;

        // Destination rect for our main canvas draw. It never changes.
        displayRect = new Rect(0, 0, width, height);
        // Scroll rect: this will be used to 'scroll around' over the
        // bitmap in memory. Initialize as above.
        scrollRect = new Rect(0, 0, width, height);
        // scrollRect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
    }

    public void setImage(Bitmap bmp) {
        if (bmLargeImage != null) {
            bmLargeImage.recycle();
        }

        bmLargeImage = bmp;
        scrollRect = new Rect(0, 0, width, height);
        scrollRectX = 0;
        scrollRectY = 0;
        scrollByX = 0;
        scrollByY = 0;
    }

    private boolean canScroll() {
        if (bmLargeImage != null) {
            return getHeight() < bmLargeImage.getHeight();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong to one of our
            // descendants.
            return false;
        }

        if (!canScroll()) {
            return false;
        }

//        if (mVelocityTracker == null) {
//            mVelocityTracker = VelocityTracker.obtain();
//        }
//        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            /*
            * If being flinged and user touches, stop the fling. isFinished
            * will be false if being flinged.
            */
//                if (!mScroller.isFinished()) {
//                    mScroller.abortAnimation();
//                }

                // Remember where the motion event started
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // Scroll to follow the motion event
                final int deltaY = (int) (mLastMotionY - y);
                mLastMotionY = y;

                notifyScroll(0, -deltaY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                final VelocityTracker velocityTracker = mVelocityTracker;
//                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
//                int initialVelocity = (int) velocityTracker.getYVelocity();
//
//                if ((Math.abs(initialVelocity) > mMinimumVelocity) && getChildCount() > 0) {
//                    fling(-initialVelocity);
//                }


        }
        return true;
    }

    public void notifyScroll(float distX, float distY) {
        scrollByX = distX; // move update x increment
        scrollByY = distY; // move update y increment
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (bmLargeImage == null) {
            return;
        }

        if (scrollByX != 0 || scrollByY != 0) {
            // Our move updates are calculated in ACTION_MOVE in the opposite     direction
            // from how we want to move the scroll rect. Think of this as
            // dragging to
            // the left being the same as sliding the scroll rect to the right.
            int newScrollRectX = scrollRectX - (int) scrollByX;
            int newScrollRectY = scrollRectY - (int) scrollByY;
            scrollByX = 0;
            scrollByY = 0;

            // Don't scroll off the left or right edges of the bitmap.
            if (newScrollRectX < 0) {
                newScrollRectX = 0;
            } else if (newScrollRectX > (bmLargeImage.getWidth() - width)) {
                newScrollRectX = (bmLargeImage.getWidth() - width);
            }

            // Don't scroll off the top or bottom edges of the bitmap.
            if (newScrollRectY < 0) {
                newScrollRectY = 0;
            } else if (newScrollRectY > (bmLargeImage.getHeight() - height)) {
                newScrollRectY = (bmLargeImage.getHeight() - height);
            }
            scrollRect.set(newScrollRectX, newScrollRectY, newScrollRectX
                    + width, newScrollRectY + height);

            scrollRectX = newScrollRectX;
            scrollRectY = newScrollRectY;
        }

        canvas.drawRect(displayRect, background);
        // We have our updated scroll rect coordinates, set them and draw.
        canvas.drawBitmap(bmLargeImage, scrollRect, displayRect, background);

    }
}
