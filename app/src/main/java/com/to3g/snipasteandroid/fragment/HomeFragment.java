package com.to3g.snipasteandroid.fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.PermissionChecker;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.permission.PermissionUtils;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.to3g.snipasteandroid.Listener.DoubleClickListener;
import com.to3g.snipasteandroid.R;
import com.to3g.snipasteandroid.base.BaseFragment;
import com.to3g.snipasteandroid.lib.ClipBoardUtil;
import com.to3g.snipasteandroid.lib.Group;
import com.to3g.snipasteandroid.lib.annotation.Widget;

import java.security.Permission;
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
        String content = ClipBoardUtil.get(Objects.requireNonNull(getContext()));
        Log.d(TAG, "剪切板内容: " + content);
        editText.setText(content);
        floatText(content);
    }

    private void showFloatText (String content) {
        // 查看是否已经创建过这个弹窗
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

    private void floatText(String content) {
        // 检查权限
        if (PermissionUtils.checkPermission(Objects.requireNonNull(getContext()))) {
            showFloatText(content);
        } else {
            // 提示用户要申请权限了
            new QMUIDialog.MessageDialogBuilder(getActivity())
//                    .setTitle("标题")
                    .setMessage("使用浮窗功能，需要您授予悬浮窗权限。")
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, "去开启", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            PermissionUtils.requestPermission(getActivity(), result -> {
                                if(result) {
                                    showFloatText(content);
                                } else {
                                    Toast.makeText(getContext(), "需要悬浮窗权限才能悬浮", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .create(R.style.QMUI_Dialog).show();
        }
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