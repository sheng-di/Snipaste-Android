package com.to3g.snipasteandroid.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.OnScaleChangedListener;

public class ScaleImage extends ImageView {

    private static final String TAG = "ScaleImage";

    private float touchDownX = 0;
    private float touchDownY = 0;

    public OnScaledListener onScaledListener;

    public interface OnScaledListener extends OnScaleChangedListener {
        void onScaled (float x, float y, MotionEvent event);
    }

    public ScaleImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return super.onTouchEvent(event);
        }
        // 屏蔽调浮窗的事件拦截，仅由自身消费
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
//                Log.d(TAG, String.format("onTouchEvent: 初始位置: %f,%f", touchDownX, touchDownY));
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, String.format("onTouchEvent: 实时位置: %f,%f", event.getX(), event.getY()));
                onScaledListener.onScaled(event.getX() - touchDownX, event.getY() - touchDownY, event);
                break;
        }
        return true;
    }
}
