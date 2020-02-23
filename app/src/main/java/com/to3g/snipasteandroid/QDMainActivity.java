package com.to3g.snipasteandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.arch.annotation.FirstFragments;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;
import com.to3g.snipasteandroid.base.BaseFragmentActivity;
import com.to3g.snipasteandroid.fragment.HomeFragment;
import com.to3g.snipasteandroid.fragment.PasteFragment;
import com.to3g.snipasteandroid.fragment.QDAboutFragment;
import com.to3g.snipasteandroid.fragment.QDTabSegmentFixModeFragment;
import com.to3g.snipasteandroid.fragment.QDWebExplorerFragment;

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
    private static final String TAG = "QDMainActivity";

    @Override
    protected int getContextViewId() {
        return R.id.snipaste_demo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected RootView onCreateRootView(int fragmentContainerId) {
        return new CustomRootView(this, fragmentContainerId);
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
