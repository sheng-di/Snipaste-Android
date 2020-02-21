package com.to3g.snipasteandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.arch.annotation.FirstFragments;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;
import com.to3g.snipasteandroid.Listener.DoubleClickListener;
import com.to3g.snipasteandroid.base.BaseFragmentActivity;
import com.to3g.snipasteandroid.fragment.HomeFragment;
import com.to3g.snipasteandroid.fragment.PasteFragment;
import com.to3g.snipasteandroid.fragment.QDAboutFragment;
import com.to3g.snipasteandroid.fragment.QDTabSegmentFixModeFragment;
import com.to3g.snipasteandroid.fragment.QDWebExplorerFragment;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import static com.to3g.snipasteandroid.fragment.QDWebExplorerFragment.EXTRA_URL;
import static com.to3g.snipasteandroid.fragment.QDWebExplorerFragment.EXTRA_TITLE;


@FirstFragments(
        value = {
                QDWebExplorerFragment.class,
                QDAboutFragment.class,
                QDTabSegmentFixModeFragment.class,
                PasteFragment.class,
                HomeFragment.class
        })
@DefaultFirstFragment(HomeFragment.class)
@LatestVisitRecord
public class QDMainActivity extends BaseFragmentActivity {
    LinearLayout mFloatLayout;
    private static final String TAG = "QDMainActivity";

    @Override
    protected int getContextViewId() {
        return R.id.snipaste_demo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        prepare();
    }

    @Override
    protected RootView onCreateRootView(int fragmentContainerId) {
        return new CustomRootView(this, fragmentContainerId);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    public static Intent of(@NonNull Context context,
                            @NonNull Class<? extends QMUIFragment> firstFragment) {
        return QMUIFragmentActivity.intentOf(context, QDMainActivity.class, firstFragment);
    }

    public static Intent of(@NonNull Context context,
                            @NonNull Class<? extends QMUIFragment> firstFragment,
                            @Nullable Bundle fragmentArgs) {
        return QMUIFragmentActivity.intentOf(context, QDMainActivity.class, firstFragment, fragmentArgs);
    }

    public static Intent createWebExplorerIntent(Context context, String url, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, url);
        bundle.putString(EXTRA_TITLE, title);
        return of(context, QDWebExplorerFragment.class, bundle);
    }
}
