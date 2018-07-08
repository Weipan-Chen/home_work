package guagua.andoird.cn.happyguagua.guaguag.android.cn.happyguagua.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import guagua.andoird.cn.happyguagua.R;

/**
 * Created by Administrator on 2018-07-07.
 */

public class GuaGuaKa extends View{

    private Paint mOutterPaint;
    private Path mPath;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private int mLastX;
    private int mLastY;

    private Bitmap mOutterBitmap;

    //----------------------
    //private Bitmap bitmap;

    private String mText;
    private Paint mBackPaint;



    //记录刮奖信息文本的宽和高
    private Rect mTextBound;
    private int mTextSize;
    private int mTextColor;

    //判断遮盖图层是否消除到60
    private volatile boolean mComplete = false;

    //刮完的回调
    public interface  OnGuaGuaKaCompleteListener{
        void complete();
    }

    private OnGuaGuaKaCompleteListener mlistener;

    public void setOnGuaGuaKaCompleteListener(OnGuaGuaKaCompleteListener mlistener) {
        this.mlistener = mlistener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 初始化bitmap
        mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        //设置绘制path画笔的属性
        setupOutPaint();
        setUpBackPaint();
        //mCanvas.drawColor(Color.parseColor("#c0c0c0"));
        mCanvas.drawRoundRect(new RectF(0,0,width,height),30,30,mOutterPaint);

        mCanvas.drawBitmap(mOutterBitmap,null,new Rect(0,0,width,height),null);
    }
    /*
    * 设置绘制获奖信息的画笔属性
    * */
    private void setUpBackPaint() {
        mBackPaint.setColor(mTextColor);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setTextSize(mTextSize);
        //获得当前画笔绘制文本的宽和高
        mBackPaint.getTextBounds(mText,0,mText.length(),mTextBound);

    }
    private void setupOutPaint() {
        //设置绘制path画笔的属性
        mOutterPaint.setColor(Color.parseColor("#c0c0c0"));
        mOutterPaint.setAntiAlias(true);
        mOutterPaint.setDither(true);
        mOutterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOutterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutterPaint.setStyle(Paint.Style.FILL);
        mOutterPaint.setStrokeWidth(20);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX,mLastY);
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = Math.abs(x-mLastX);
                int dy = Math.abs(y-mLastY);
                if(dx>3 || dy>3) {
                    mPath.lineTo(x,y);
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                new Thread(mRunnable).start();
                break;
            default:
                break;
        }
        invalidate();
        return true;

    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            int w = getWidth();
            int h = getHeight();

            float wipeArea = 0;
            float totalArea = w*h;

            Bitmap bitmap = mBitmap;
            int[] mPixels = new int[w*h];
            //获得bitmap上所有像素信息
            bitmap.getPixels(mPixels,0,w,0,0,w,h);

            for(int i=0; i<w; i++) {
                for(int j=0; j<h; j++) {
                    int index = i + j*w;
                    if(mPixels[index] == 0) {
                        wipeArea++;
                    }
                }
            }

            if(wipeArea>0 && totalArea>0) {
                int percent = (int)(wipeArea*100/totalArea);
                Log.e("TAG",percent+"");
                if(percent > 60) {
                    //清除吊图层区域
                    mComplete = true;
                    postInvalidate();

                }
            }
        }
    };


    @Override
    protected void onDraw(Canvas canvas) {

        //canvas.drawBitmap(bitmap,0,0,null);
        canvas.drawText(mText,getWidth()/2-mTextBound.width()/2,getHeight()/2+mTextBound.height()/2,mBackPaint);

        if(mComplete) {
            if(mlistener != null) {
                mlistener.complete();
            }
        }
        if(!mComplete) {
            drawPath();
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    private void drawPath() {
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        mCanvas.drawPath(mPath,mOutterPaint);
    }

    //初始化
    private void init() {
        mOutterPaint = new Paint();
        mPath = new Path();
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jojobg);
        mOutterBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.jojobg);
        mText = "谢谢惠顾";
        mTextBound = new Rect();
        mBackPaint = new Paint();
        mTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,22,getResources().getDisplayMetrics());
    }

    public GuaGuaKa(Context context, AttributeSet attrs) {
        this(context,attrs,0);

    }

    public GuaGuaKa(Context context) {
        this(context,null);

    }

    public GuaGuaKa(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();

        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs,R.styleable.GuaGuaKa,defStyleAttr,0);
        int n = a.getIndexCount();
        for(int i=0;i<n;i++) {
            int attr = a.getIndex(i);
            switch(attr) {
                case R.styleable.GuaGuaKa_text:
                    mText = a.getString(attr);
                    break;
                case R.styleable.GuaGuaKa_textColor:
                    mTextColor = a.getColor(attr,0x000000);
                    break;
                case R.styleable.GuaGuaKa_textSize:
                    mTextSize = (int)a.getDimension(attr,
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,22,getResources().getDisplayMetrics()));
                    break;
            }

        }
        if(a!=null) {
            a.recycle();
        }

    }

    public void setText(String mText) {
        this.mText = mText;
        //文本狂傲
        mBackPaint.getTextBounds(mText,0,mText.length(),mTextBound);

    }

}
