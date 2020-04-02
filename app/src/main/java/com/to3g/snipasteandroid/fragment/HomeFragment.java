package com.to3g.snipasteandroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.permission.PermissionUtils;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.ramotion.fluidslider.FluidSlider;
import com.to3g.snipasteandroid.Listener.DoubleClickListener;
import com.to3g.snipasteandroid.R;
import com.to3g.snipasteandroid.base.BaseFragment;
import com.to3g.snipasteandroid.lib.ClipBoardUtil;
import com.to3g.snipasteandroid.lib.GlideEngine;
import com.to3g.snipasteandroid.lib.Group;
import com.to3g.snipasteandroid.lib.ImageUtil;
import com.to3g.snipasteandroid.lib.annotation.Widget;
import com.to3g.snipasteandroid.receiver.MyReceiver;
import com.to3g.snipasteandroid.receiver.MyReceiverHandler;
import com.to3g.snipasteandroid.service.NotificationService;
import com.to3g.snipasteandroid.view.ScaleImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.bolt.screenshotty.ScreenshotBitmap;
import eu.bolt.screenshotty.ScreenshotManager;
import eu.bolt.screenshotty.ScreenshotManagerBuilder;
import eu.bolt.screenshotty.ScreenshotResult;
import kotlin.Unit;

@LatestVisitRecord
@Widget(group = Group.Other, name = "Home")
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.pasteTextButton)
    QMUIRoundButton pasteTextButton;

    @BindView(R.id.pasteClipboardButton)
    QMUIRoundButton pasteClipboardButton;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.albumButton)
    QMUIRoundButton albumButton;

    @BindView(R.id.cameraButton)
    QMUIRoundButton cameraButton;

    @BindView(R.id.slider)
    FluidSlider slider;

    private List<String> floatings = new ArrayList<>();
    private List<String> floatingImages = new ArrayList<>();
    private float opacity = 1.0f;

    private ScreenshotManager screenshotManager;
    private int REQUEST_SCREENSHOT_PERMISSION = 888;
    private MyReceiver myReceiver;


    @Override
    protected View onCreateView() {
        // bind view
        View root = LayoutInflater.from(getContext()).inflate(R.layout.home_layout, null);
        ButterKnife.bind(this, root);
        // init the top bar
        initTopBar();

        // init the bottom slider
        slider.setPosition(1);
        slider.setPositionListener(pos -> {
            opacity = pos;
            setFloatViewOpacity();
            return Unit.INSTANCE;
        });
        setFloatViewOpacity();
        initReceiver();
        return root;
    }

    private void initReceiver () {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyReceiver.ACTION_SCREENSHOT);
        getContext().registerReceiver(myReceiver, intentFilter);
        myReceiver.setMyReceiverHandler(new MyReceiverHandler() {
            @Override
            public void doScreenshot() {
                Toast.makeText(getContext(), "doScreenshot", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFloatViewOpacity () {
        for (String content: floatings) {
            View view = EasyFloat.getAppFloatView(content);
            if (view != null) {
                view.findViewById(R.id.textBackground).setAlpha(opacity);
            }
        }
        for (String path: floatingImages) {
            View view = EasyFloat.getAppFloatView(path);
            if (view != null) {
                view.findViewById(R.id.imageOutterShadow).setAlpha(opacity);
            }
        }
    }

    private void pasteCamera () {
        PictureSelector
                .create(getActivity())
                .openCamera(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .enableCrop(true)
                .freeStyleCropEnabled(true)
                .forResult(result -> {
                    if (result.size() > 0) {
                        LocalMedia localMedia = result.get(0);
                        String path = localMedia.getCutPath();
                        Log.d(TAG, "onResult: " + path);
                        File file = new File(path);
                        if (file.exists()) {
                            initImageView(path);
                        }
                    }
                });
    }

    /**
     * When click the camera button
     */
    @OnClick(R.id.cameraButton)
    protected void onCameraButtonClick () {
        // check the permission
        if (PermissionUtils.checkPermission(Objects.requireNonNull(getContext()))) {
            pasteCamera();
        } else {
            // prompt to request permission
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setMessage(getText(R.string.floatingPermissionText))
                    .addAction(getText(R.string.cancelText), new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, getText(R.string.toOpen), QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            PermissionUtils.requestPermission(getActivity(), result -> {
                                if(result) {
                                    pasteCamera();
                                } else {
                                    Toast.makeText(getContext(), getText(R.string.needFloatingPermission), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .create(R.style.QMUI_Dialog).show();
        }
    }

    private void pasteAlbum () {
        PictureSelector
                .create(getActivity())
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .enableCrop(true)
                .freeStyleCropEnabled(true)
                .selectionMode(PictureConfig.SINGLE)
                .isSingleDirectReturn(true)
                .forResult(result -> {
                    if (result.size() > 0) {
                        LocalMedia localMedia = result.get(0);
                        String path = localMedia.getCutPath();
                        Log.d(TAG, "onResult: " + path);
                        File file = new File(path);
                        if (file.exists()) {
                            initImageView(path);
                        }
                    }
                });
    }

    /**
     * When click the album button
     */
    @OnClick(R.id.albumButton)
    protected void onAlbumButtonClick () {
        // check the permission
        if (PermissionUtils.checkPermission(Objects.requireNonNull(getContext()))) {
            pasteAlbum();
        } else {
            // prompt to request permission
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setMessage(getText(R.string.floatingPermissionText))
                    .addAction(getText(R.string.cancelText), new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, getText(R.string.toOpen), QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            PermissionUtils.requestPermission(getActivity(), result -> {
                                if(result) {
                                    pasteAlbum();
                                } else {
                                    Toast.makeText(getContext(), getText(R.string.needFloatingPermission), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .create(R.style.QMUI_Dialog).show();
        }
    }

    /**
     * When click the screenButton
     */
    @OnClick(R.id.screenButton)
    protected void onScreenButtonClick () {
//        pasteScreenshot();
        Intent intent = new Intent(getContext(), NotificationService.class);
        getContext().startService(intent);
    }

    private void pasteScreenshot () {
        screenshotManager = new ScreenshotManagerBuilder(getActivity())
                .withPermissionRequestCode(REQUEST_SCREENSHOT_PERMISSION)
                .build();
        ScreenshotResult screenshotResult = screenshotManager.makeScreenshot();
        screenshotResult.observe((screenshot) -> {
            initImageView(((ScreenshotBitmap) screenshot).getBitmap());
            return Unit.INSTANCE;
        }, (throwable -> {
            return Unit.INSTANCE;
        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        screenshotManager.onActivityResult(requestCode, requestCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private ViewGroup.LayoutParams getDefaultParams (String path, ViewGroup.LayoutParams layoutParams) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // 获取图片宽高
        Size size = ImageUtil.getImageSize(path);
        int imgWidth = size.getWidth();
        int imgHeight = size.getHeight();
        Log.d(TAG, String.format("initImageView: image size：%d, %d", imgWidth, imgHeight));

        float rate = 0.8f;

        layoutParams.width = (int) (rate * screenWidth);
        layoutParams.height = (int) (layoutParams.width * 1.0f / imgWidth * imgHeight);
        Log.d(TAG, String.format("initImageView: layout size：%d, %d", layoutParams.width, layoutParams.height));
        return layoutParams;
    }

    private ViewGroup.LayoutParams getDefaultParams (Bitmap bitmap, ViewGroup.LayoutParams layoutParams) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // 获取图片宽高
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        Log.d(TAG, String.format("initImageView: image size：%d, %d", imgWidth, imgHeight));

        float rate = 0.8f;

        layoutParams.width = (int) (rate * screenWidth);
        layoutParams.height = (int) (layoutParams.width * 1.0f / imgWidth * imgHeight);
        Log.d(TAG, String.format("initImageView: layout size：%d, %d", layoutParams.width, layoutParams.height));
        return layoutParams;
    }

    private void initImageView(String path) {
        EasyFloat
                .with(Objects.requireNonNull(getActivity()))
                .setLayout(R.layout.image_paste)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(100, 200)
                .setTag(path)
                .show();
        floatingImages.add(path);
        View view = EasyFloat.getAppFloatView(path);
        assert view != null;
        View imageOutter = view.findViewById(R.id.imageOutter);
        View imageOutterShadow = view.findViewById(R.id.imageOutterShadow);

        ViewGroup.LayoutParams layoutParams = imageOutterShadow.getLayoutParams();
        imageOutterShadow.setLayoutParams(getDefaultParams(path, layoutParams));

        imageOutter.setBackground(Drawable.createFromPath(path));

        ScaleImage scaleImage = view.findViewById(R.id.scaleImage);
        scaleImage.onScaledListener = new ScaleImage.OnScaledListener() {
            @Override
            public void onScaled(float x, float y, MotionEvent event) {
                layoutParams.width = (int) (layoutParams.width + x);
                layoutParams.height = (int) (layoutParams.height + y);
                imageOutterShadow.setLayoutParams(layoutParams);
            }

            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {

            }
        };
        view.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                EasyFloat.dismissAppFloat(path);
                floatingImages.remove(path);
            }
        });
        setFloatViewOpacity();
    }

    private void initImageView(Bitmap bitmap) {
        String tagName = "bitmap";
        EasyFloat
                .with(Objects.requireNonNull(getActivity()))
                .setLayout(R.layout.image_paste)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(100, 200)
                .setTag(tagName)
                .show();
        floatingImages.add(tagName);
        View view = EasyFloat.getAppFloatView(tagName);
        assert view != null;
        View imageOutter = view.findViewById(R.id.imageOutter);
        View imageOutterShadow = view.findViewById(R.id.imageOutterShadow);

        ViewGroup.LayoutParams layoutParams = imageOutterShadow.getLayoutParams();
        imageOutterShadow.setLayoutParams(getDefaultParams(bitmap, layoutParams));

        imageOutter.setBackground(new BitmapDrawable(bitmap));

        ScaleImage scaleImage = view.findViewById(R.id.scaleImage);
        scaleImage.onScaledListener = new ScaleImage.OnScaledListener() {
            @Override
            public void onScaled(float x, float y, MotionEvent event) {
                layoutParams.width = (int) (layoutParams.width + x);
                layoutParams.height = (int) (layoutParams.height + y);
                imageOutterShadow.setLayoutParams(layoutParams);
            }

            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {

            }
        };
        view.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                EasyFloat.dismissAppFloat(tagName);
                floatingImages.remove(tagName);
            }
        });
        setFloatViewOpacity();
    }

    @OnClick(R.id.pasteTextButton)
    protected void onPasteTextButtonClick () {
        floatText(editText.getText().toString());
    }

    @OnClick(R.id.pasteClipboardButton)
    protected void onPasteClickboardButtonClick () {
        String content = ClipBoardUtil.get(Objects.requireNonNull(getContext()));
        Log.d(TAG, "clipboard content: " + content);
        editText.setText(content);
        floatText(content);
    }

    private void showFloatText (String content) {
        // check if the floating window has been created
        View view = EasyFloat.getAppFloatView(content);
        if (content == null || content.equals("")) {
            Toast.makeText(getContext(), getText(R.string.blankContent), Toast.LENGTH_SHORT).show();
            return;
        }
        // if alerady created, return false
        if (view != null) {
            Toast.makeText(getContext(), getText(R.string.textFloated), Toast.LENGTH_SHORT).show();
            return;
        }
        // create new one
        EasyFloat
                .with(Objects.requireNonNull(getActivity()))
                .setLayout(R.layout.text_paste)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setLocation(100, 200)
                .setTag(content)
                .show();
        floatings.add(content);
        view = EasyFloat.getAppFloatView(content);
        assert view != null;
        TextView textView = view.findViewById(R.id.textView);
        view.findViewById(R.id.textView).setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                EasyFloat.dismissAppFloat(content);
                floatings.remove(content);
            }
        });
        textView.setText(content);
        setFloatViewOpacity();
    }

    private void floatText(String content) {
        // check the permission
        if (PermissionUtils.checkPermission(Objects.requireNonNull(getContext()))) {
            showFloatText(content);
        } else {
            // prompt to request permission
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setMessage(getText(R.string.floatingPermissionText))
                    .addAction(getText(R.string.cancelText), new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, getText(R.string.toOpen), QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            PermissionUtils.requestPermission(getActivity(), result -> {
                                if(result) {
                                    showFloatText(content);
                                } else {
                                    Toast.makeText(getContext(), getText(R.string.needFloatingPermission), Toast.LENGTH_SHORT).show();
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
                .setOnClickListener(v -> {
                   clearAllTextFloatViews();
                });
    }

    /**
     * Clear all text floating windows
     */
    private void clearAllTextFloatViews () {
        for (String content : floatings) {
            if (EasyFloat.getAppFloatView(content) != null) {
                EasyFloat.dismissAppFloat(content);
            }
        }
        for (String path: floatingImages) {
            if (EasyFloat.getAppFloatView(path) != null) {
                EasyFloat.dismissAppFloat(path);
            }
        }
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
