package com.example.zhiwenyan.guaguaka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhiwenyan on 4/23/16.
 */
public class GuaView extends View {
    private Paint paint;
    private Path path;
    private Canvas canvas;
    private Bitmap bitmap;

    private int LastX;
    private int LastY;


    private Bitmap bitmap1;

    private String Text;
    private Paint BackPaint;
    /**
     * 记录刮奖信息文本的宽和高
     */
    private Rect rect;


    private volatile boolean mComplete = false;

    private onCompleteListener onCompleteListener;

    public GuaView(Context context) {
        this(context, null);

    }

    public GuaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        path = new Path();
        bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.fg_guaguaka);

        Text = "5000000,00";
        BackPaint = new Paint();
        rect = new Rect();
        BackPaint.setColor(Color.BLACK);
        BackPaint.setTextSize(20);
        BackPaint.setAntiAlias(true);
        // BackPaint.set
        //文本的宽和高
        BackPaint.getTextBounds(Text, 0, Text.length(), rect);


    }

    /*
    获取控件的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        //初始化Bitmap
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        canvas.drawBitmap(bitmap1, 0, 0, null);

        //  canvas.drawColor(Color.parseColor("#c0c0c0"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LastX = x;
                LastY = y;
                path.moveTo(LastX, LastY);
                break;
            case MotionEvent.ACTION_MOVE:
                //     int dx = Math.abs(x - LastX);
                //   int dy = Math.abs(y - LastY);
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                new Thread(runnable).start();
                break;
        }
        invalidate();
        return true;
    }

    public void setOnCompleteListener(onCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;

    }

    public interface onCompleteListener {
        void onComplete();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int width = getWidth();
            int height = getHeight();
            float wipeArea = 0;
            float totalArea = width * height;
            Bitmap bitmap = bitmap1;
            int[] mPixels = new int[width * height];
            //获取bitmap1的像素信息
            bitmap.getPixels(mPixels, 0, width, 0, 0, width, height);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int index = i + j * width;
                    if (mPixels[index] == 0) {
                        wipeArea++;
                    }
                }
            }
            if (wipeArea > 0 && totalArea > 0) {
                int parcent = (int) ((wipeArea * 100) / totalArea);
                System.out.println("----" + parcent);
                if (parcent > 60) {
                    mComplete = true;
                    postInvalidate();
                }
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(Text, getWidth() / 2 - rect.width() / 2,
                getHeight() / 2 + rect.height() / 2, BackPaint);

        if (onCompleteListener != null) {
            if (mComplete) {
                onCompleteListener.onComplete();
            }
        }
        if (!mComplete) {
            drawPath();
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        super.onDraw(canvas);
    }

    private void drawPath() {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawPath(path, paint);
    }
}
