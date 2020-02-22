package com.to3g.snipasteandroid.Listener;

import android.view.View;

public  abstract class DoubleClickListener implements View.OnClickListener {
    private static final long DOUBLE_TIME = 500;
    private static long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastClickTime < DOUBLE_TIME) {
            onDoubleClick(v);
        }
        lastClickTime = currentTimeMillis;
    }

    public abstract void onDoubleClick(View v);
}