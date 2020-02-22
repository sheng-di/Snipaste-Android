package com.to3g.snipasteandroid.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
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

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.home_layout, null);
        ButterKnife.bind(this, root);
        titleView1.setText("这是动态设置的标题");
        initTopBar();
        pasteTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = ClipBoardUtil.get(getContext());
                Log.d(TAG, "剪切板内容: " + content);
            }
        });
        return root;
    }

    private void initTopBar() {
        mTopBar.setTitle(getString(R.string.app_name));
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, R.id.topbar_right_change_button)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "点击干嘛", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Object onLastFragmentFinish() {
        return null;
    }

    @Override
    protected boolean canDragBack(Context context, int dragDirection, int moveEdge) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (content != null) {
//            editText.setText(content);
//        }
    }
}
