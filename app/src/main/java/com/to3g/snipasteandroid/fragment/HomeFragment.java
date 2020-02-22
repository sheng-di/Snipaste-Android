package com.to3g.snipasteandroid.fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.to3g.snipasteandroid.Listener.DoubleClickListener;
import com.to3g.snipasteandroid.R;
import com.to3g.snipasteandroid.base.BaseFragment;
import com.to3g.snipasteandroid.lib.ClipBoardUtil;
import com.to3g.snipasteandroid.lib.Group;
import com.to3g.snipasteandroid.lib.annotation.Widget;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

@LatestVisitRecord
@Widget(group = Group.Other, name = "Home")
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";

    @BindView(R.id.home_title1)
    TextView titleView1;

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.pasteTextButton)
    QMUIRoundButton pasteTextButton;

    @BindView(R.id.pasteClipboardButton)
    QMUIRoundButton pasteClipboardButton;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @Override
    protected View onCreateView() {
        // 绑定视图
        View root = LayoutInflater.from(getContext()).inflate(R.layout.home_layout, null);
        ButterKnife.bind(this, root);
        // 初始化TopBar
        initTopBar();
        // 绑定按钮点击事件
        pasteClipboardButton.setOnClickListener(v -> {
            onPasteClickboardButtonClick();
        });
        pasteTextButton.setOnClickListener(v -> {
            onPasteTextButtonClick();
        });
        return root;
    }

    private void onPasteTextButtonClick () {
        floatText(editText.getText().toString());
    }

    private void onPasteClickboardButtonClick () {
        String content = ClipBoardUtil.get(getContext());
        Log.d(TAG, "剪切板内容: " + content);
        editText.setText(content);
        floatText(content);
    }

    private void floatText(String content) {
        View view = EasyFloat.getAppFloatView(content);
        if (content == null || content.equals("")) {
            Toast.makeText(getContext(), "内容为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 如果已经创建，返回 false
        if (view != null) {
            Toast.makeText(getContext(), "已悬浮", Toast.LENGTH_SHORT).show();
            return;
        }
        // 创建一个新的
        EasyFloat
                .with(Objects.requireNonNull(getActivity()))
                .setLayout(R.layout.text_paste)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(100, 200)
                .setTag(content)
                .show();
        view = EasyFloat.getAppFloatView(content);
        assert view != null;
        TextView textView = view.findViewById(R.id.textView);
        view.findViewById(R.id.textView).setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                EasyFloat.dismissAppFloat(content);
            }
        });
        textView.setText(content);
    }

    private void initTopBar() {
        mTopBar.setTitle(getString(R.string.app_name));
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, R.id.topbar_right_change_button)
                .setOnClickListener(v -> Toast.makeText(getContext(), "点击干嘛", Toast.LENGTH_SHORT).show());
    }

    @Override
    public Object onLastFragmentFinish() {
        return null;
    }

    @Override
    protected boolean canDragBack(Context context, int dragDirection, int moveEdge) {
        return false;
    }
}
