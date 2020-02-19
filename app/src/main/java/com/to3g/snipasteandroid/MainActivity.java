package com.to3g.snipasteandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.to3g.snipasteandroid.Listener.DoubleClickListener;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

public class MainActivity extends AppCompatActivity {
    LinearLayout mFloatLayout;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepare();
    }

    private void prepare () {
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.text_paste, null);

        mFloatLayout.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                FloatWindow.destroy();
            }
        });
    }

    public void ShowFloat () {
        FloatWindow
                .with(getApplicationContext())
                .setView(mFloatLayout)
                .setX(100)
                .setY(Screen.height, 0.3f)
                .setDesktopShow(true)
                .setMoveType(MoveType.active)
                .build();
        FloatWindow.get().show();
    }

    public static String getClipContent(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        assert clipboardManager != null;
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            CharSequence text = clipData.getItemAt(0).getText();
            if (text != null) {
                return text.toString();
            }
        }
        return null;
    }

    public void onButtonClick(View view) {
        String content = getClipContent(this);
        Log.d(TAG, "onButtonClick: " + content);
        if (content != null) {
            ShowFloat();
            TextView textView = mFloatLayout.findViewById(R.id.textView);
            textView.setText(content);
        } else {
            Toast.makeText(this, "剪贴板为空", Toast.LENGTH_SHORT).show();
        }
    }
}
