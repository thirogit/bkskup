package com.bk.bkskup3.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.bk.bkskup3.R;

public class ViewfinderView extends View {

    private final Paint paint;
    private final int maskColor;

    public ViewfinderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        Resources resources = getResources();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskColor = resources.getColor(R.color.viewfinder_mask);
//        scannerAlpha = 0;

    }

    public void onDraw(Canvas canvas) {


        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int middle = height/2;
        int left = 50;
        int top = middle - 100;
        int right = width - 50;
        int bottom = middle + 100;

        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, top, paint);
        canvas.drawRect(0, top, left, bottom + 1, paint);
        canvas.drawRect(right + 1, top, width, bottom + 1, paint);
        canvas.drawRect(0, bottom + 1, width, height, paint);

    }



}
