package com.to3g.snipasteandroid.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class GestureImageView extends ImageView implements View.OnTouchListener {

    private static final String TAG = "GestureImageView";

    public class ZoomMode{
        public final static int Ordinary = 0;
        public final static int ZoomIn = 1;
        public final static int DoubleZoomIn = 2;
    }

    private  int curMode=0;
    private Matrix matrix;
    private PointF viewSize;
    private  PointF imageSize;
    private  PointF scaleSize;

    //记录图片当前坐标
    private  PointF curPoint;
    private  PointF originScale;

    //0:宽度适应 1:高度适应
    private  int fitMode=0;
    private  PointF start;
    private  PointF center;
    private  float scaleDoubleZoom=0;
    private PointF relativePoint;
    private  float doubleFingerDistance=0;

    long doubleClickTimeSpan=280;
    long lastClickTime=0;
    int rationZoomIn=2;

    public  void GestureImageViewInit(){
        this.setOnTouchListener(this);
        this.setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();
        originScale = new PointF();
        scaleSize = new PointF();
        start = new PointF();
        center = new PointF();
        curPoint = new PointF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        viewSize = new PointF(width, height);

        //获取当前Drawable的大小
        Drawable drawable = getDrawable();
        if (drawable == null) {
            Log.e("no drawable","drawable is nullPtr");
            imageSize = new PointF(width, height/2);
        }else {
            imageSize = new PointF(drawable.getMinimumWidth(),drawable.getMinimumHeight());
        }
        FitCenter();
    }

    /**
     * 使图片保存在中央
     */
    public void FitCenter(){
        float scaleH = viewSize.y/imageSize.y;
        float scaleW = viewSize.x/imageSize.x;
        //选择小的缩放因子确保图片全部显示在视野内
        float scale = scaleH<scaleW?scaleH:scaleW;
        //根据view适应大小
        setImageScale(new PointF(scale, scale));

        originScale.set(scale, scale);
        //根据缩放因子大小来将图片中心调整到view 中心
        if (scaleH < scaleW) {
            setImageTranslation(new PointF(viewSize.x / 2 - scaleSize.x / 2, 0));
            fitMode = 1;
        } else {
            fitMode = 0;
            setImageTranslation(new PointF(0, viewSize.y / 2 - scaleSize.y / 2));
        }
        //记录缩放因子 下次继续从这个比例缩放
        scaleDoubleZoom = originScale.x;
    }

    public GestureImageView(Context context) {
        super(context);
        GestureImageViewInit();
    }

    public GestureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        GestureImageViewInit();
    }

    public GestureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        GestureImageViewInit();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getX(), event.getY());
                //手指按下事件
                if (event.getPointerCount() == 1) {
                    if (event.getEventTime()-lastClickTime <= doubleClickTimeSpan) {
                        //双击事件触发
                        Log.e("TouchEvent", "DoubleClick");
                        if (curMode == ZoomMode.Ordinary) {
                            curMode = ZoomMode.ZoomIn;
                            relativePoint = new PointF();
                            //计算归一化坐标
                            relativePoint.set( (start.x-curPoint.x)/scaleSize.x, (start.y-curPoint.y)/scaleSize.y );

                            setImageScale(new PointF(originScale.x * rationZoomIn, originScale.y * rationZoomIn));
                            setImageTranslation(new PointF(start.x - relativePoint.x * scaleSize.x, start.y - relativePoint.y * scaleSize.y));
                        } else {
                            curMode = ZoomMode.Ordinary;
                            FitCenter();
                        }
                    } else {
                        lastClickTime = event.getEventTime();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
//                v.getParent().requestDisallowInterceptTouchEvent(true);
                //屏幕上已经有一个点按住 再按下一点时触发该事件
                doubleFingerDistance = getDoubleFingerDistance(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
//                v.getParent().requestDisallowInterceptTouchEvent(true);
                //屏幕上已经有两个点按住 再松开一点时触发该事件
                curMode = ZoomMode.ZoomIn;
                scaleDoubleZoom = scaleSize.x/imageSize.x;
                if (scaleSize.x<viewSize.x && scaleSize.y<viewSize.y) {
                    curMode = ZoomMode.Ordinary;
                    FitCenter();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                v.getParent().requestDisallowInterceptTouchEvent(true);
                //手指移动时触发事件
                if (event.getPointerCount() == 1) {
                    if (curMode == ZoomMode.ZoomIn) {
                        setImageTranslation(new PointF(event.getX() - start.x,  event.getY() - start.y));
                        start.set(event.getX(),event.getY());
                    }
                } else {
                    //双指缩放时判断是否满足一定距离
                    if (Math.abs(getDoubleFingerDistance(event) - doubleFingerDistance) > 50 && curMode != ZoomMode.DoubleZoomIn) {
                        //获取双指中点
                        center.set((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2);
                        //设置起点
                        start.set(center);
                        curMode = ZoomMode.DoubleZoomIn;
                        doubleFingerDistance = getDoubleFingerDistance(event);
                        relativePoint = new PointF();

                        //根据图片当前坐标值计算归一化坐标
                        relativePoint.set(( start.x-curPoint.x )/ scaleSize.x,(start.y-curPoint.y)/scaleSize.y);
                    }

                    if(curMode==ZoomMode.DoubleZoomIn) {
                        float scale =scaleDoubleZoom*getDoubleFingerDistance(event)/doubleFingerDistance;
                        Log.d(TAG, "onTouch: " + scale);
                        setImageScale(new PointF(scale, scale));
                        setImageTranslation(new PointF(start.x - relativePoint.x * scaleSize.x, start.y - relativePoint.y * scaleSize.y));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //手指松开时触发事件
//                v.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }

        //注意这里return 的一定要是true 否则只会触发按下事件
        return true;
    }


    /**
     * 根据缩放因子缩放图片
     * @param scale
     */
    public  void setImageScale(PointF scale){
        matrix.setScale(scale.x, scale.y);
        scaleSize.set(scale.x*imageSize.x, scale.y*imageSize.y);
        this.setImageMatrix(matrix);
    }

    /**
     * 根据偏移量改变图片位置
     * @param offset
     */
    public void setImageTranslation (PointF offset) {
        matrix.postTranslate(offset.x, offset.y);
        curPoint.set(offset);
        this.setImageMatrix(matrix);
    }

    public static float getDoubleFingerDistance (MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

}