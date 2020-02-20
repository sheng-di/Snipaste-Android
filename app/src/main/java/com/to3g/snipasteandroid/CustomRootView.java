package com.to3g.snipasteandroid;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.skin.QMUISkinHelper;
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.util.QMUIViewOffsetHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView2;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;

class CustomRootView extends QMUIFragmentActivity.RootView {

    private QMUIWindowInsetLayout fragmentContainer;
    private QMUIRadiusImageView2 globalBtn;
    private QMUIViewOffsetHelper globalBtnOffsetHelper;
    private int btnSize;
    private final int touchSlop;
    private float touchDownX = 0;
    private float touchDownY = 0;
    private float lastTouchX = 0;
    private float lastTouchY = 0;
    private boolean isDragging;
    private boolean isTouchDownInGlobalBtn = false;

    public CustomRootView(Context context, int fragmentContainerId) {
        super(context);

        btnSize = QMUIDisplayHelper.dp2px(context, 56);

        fragmentContainer = new QMUIWindowInsetLayout(context);
        fragmentContainer.setId(fragmentContainerId);
        addView(fragmentContainer, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        globalBtn = new QMUIRadiusImageView2(context);
        globalBtn.setImageResource(R.mipmap.icon_theme);
        globalBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        globalBtn.setRadiusAndShadow(btnSize / 2,
                QMUIDisplayHelper.dp2px(getContext(), 16), 0.4f);
        globalBtn.setBorderWidth(1);
        globalBtn.setBorderColor(QMUIResHelper.getAttrColor(context, R.attr.qmui_skin_support_color_separator));
        globalBtn.setBackgroundColor(QMUIResHelper.getAttrColor(context, R.attr.app_skin_common_background));
        globalBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        FrameLayout.LayoutParams globalBtnLp = new FrameLayout.LayoutParams(btnSize, btnSize);
        globalBtnLp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        globalBtnLp.bottomMargin = QMUIDisplayHelper.dp2px(context, 60);
        globalBtnLp.rightMargin = QMUIDisplayHelper.dp2px(context, 24);
        QMUISkinValueBuilder builder = QMUISkinValueBuilder.acquire();
        builder.background(R.attr.app_skin_common_background);
        builder.border(R.attr.qmui_skin_support_color_separator);
        builder.tintColor(R.attr.app_skin_common_img_tint_color);
        QMUISkinHelper.setSkinValue(globalBtn, builder);
        builder.release();
        addView(globalBtn, globalBtnLp);
        globalBtnOffsetHelper = new QMUIViewOffsetHelper(globalBtn);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        globalBtnOffsetHelper.onViewLayout();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            isTouchDownInGlobalBtn = isDownInGlobalBtn(x, y);
            touchDownX = lastTouchX = x;
            touchDownY = lastTouchY = y;
        }else if(action == MotionEvent.ACTION_MOVE){
            if(!isDragging && isTouchDownInGlobalBtn){
                int dx = (int) (x - touchDownX);
                int dy = (int) (y - touchDownY);
                if(Math.sqrt(dx * dx + dy * dy) > touchSlop){
                    isDragging = true;
                }
            }

            if(isDragging){
                int dx = (int) (x - lastTouchX);
                int dy = (int) (y - lastTouchY);
                int gx = globalBtn.getLeft();
                int gy = globalBtn.getTop();
                int gw = globalBtn.getWidth(), w = getWidth();
                int gh = globalBtn.getHeight(), h = getHeight();
                if(gx + dx < 0){
                    dx = -gx;
                }else if(gx + dx + gw > w){
                    dx = w - gw - gx;
                }

                if(gy + dy < 0){
                    dy = - gy;
                }else if(gy + dy + gh > h){
                    dy = h - gh - gy;
                }
                globalBtnOffsetHelper.setLeftAndRightOffset(
                        globalBtnOffsetHelper.getLeftAndRightOffset() + dx);
                globalBtnOffsetHelper.setTopAndBottomOffset(
                        globalBtnOffsetHelper.getTopAndBottomOffset() + dy);
            }
            lastTouchX = x;
            lastTouchY = y;
        } else if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP){
            isDragging = false;
            isTouchDownInGlobalBtn = false;
        }
        return isDragging;
    }

    private boolean isDownInGlobalBtn(float x, float y){
        return globalBtn.getLeft() < x && globalBtn.getRight() > x &&
                globalBtn.getTop() < y && globalBtn.getBottom() > y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            isTouchDownInGlobalBtn = isDownInGlobalBtn(x, y);
            touchDownX = lastTouchX = x;
            touchDownY = lastTouchY = y;
        }else if(action == MotionEvent.ACTION_MOVE){
            if(!isDragging && isTouchDownInGlobalBtn){
                int dx = (int) (x - touchDownX);
                int dy = (int) (y - touchDownY);
                if(Math.sqrt(dx * dx + dy * dy) > touchSlop){
                    isDragging = true;
                }
            }

            if(isDragging){
                int dx = (int) (x - lastTouchX);
                int dy = (int) (y - lastTouchY);
                int gx = globalBtn.getLeft();
                int gy = globalBtn.getTop();
                int gw = globalBtn.getWidth(), w = getWidth();
                int gh = globalBtn.getHeight(), h = getHeight();
                if(gx + dx < 0){
                    dx = -gx;
                }else if(gx + dx + gw > w){
                    dx = w - gw - gx;
                }

                if(gy + dy < 0){
                    dy = - gy;
                }else if(gy + dy + gh > h){
                    dy = h - gh - gy;
                }
                globalBtnOffsetHelper.setLeftAndRightOffset(
                        globalBtnOffsetHelper.getLeftAndRightOffset() + dx);
                globalBtnOffsetHelper.setTopAndBottomOffset(
                        globalBtnOffsetHelper.getTopAndBottomOffset() + dy);
            }
            lastTouchX = x;
            lastTouchY = y;
        } else if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP){
            isDragging = false;
            isTouchDownInGlobalBtn = false;
        }
        return isDragging || super.onTouchEvent(event);
    }
}
