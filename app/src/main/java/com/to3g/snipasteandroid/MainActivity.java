package com.to3g.snipasteandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.Screen;

public class MainActivity extends AppCompatActivity {
    LinearLayout mFloatLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFloat();
    }

    private void showFloat () {
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.text_paste, null);
        FloatWindow
                .with(getApplicationContext())
                .setView(mFloatLayout)
                .setWidth(100)
                .setHeight(Screen.width, 0.2f)
                .setX(100)
                .setY(Screen.height, 0.3f)
                .setDesktopShow(true)
                .build();

    }
}
