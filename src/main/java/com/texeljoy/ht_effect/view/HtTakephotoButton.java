package com.texeljoy.ht_effect.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.texeljoy.ht_effect.R;

/**
 * @ClassName HtTakephotoButton
 * @Description TODO
 * @Author lu guaiguai
 * @Date 2023/4/20 9:44
 */
public class HtTakephotoButton extends View {
    private float circleWidth;//外圆环宽度
    private int outCircleColor;//外圆颜色
    private int innerCircleColor;//内圆颜色
    private int progressColor;//进度条颜色

    private Paint outRoundPaint = new Paint(); //外圆画笔
    private Paint mCPaint = new Paint();//进度画笔
    private Paint innerRoundPaint = new Paint();
    private float width; //自定义view的宽度
    private float height; //自定义view的高度
    private float outRaduis; //外圆半径
    private float innerRaduis;//内圆半径
    private GestureDetectorCompat mDetector;//手势识别
    private boolean isLongClick;//是否长按
    private float startAngle = -90;//开始角度
    private float mmSweepAngleStart = 0f;//起点
    private float mmSweepAngleEnd = 360f;//终点
    private float mSweepAngle;//扫过的角度
    private int mLoadingTime;


    public HtTakephotoButton(Context context) {
        this(context,null);
    }

    public HtTakephotoButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }

    public HtTakephotoButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HtTakephotoButton);
        outCircleColor = array.getColor(R.styleable.HtTakephotoButton_outCircleColor, Color.parseColor("#00E0E0E0"));
        innerCircleColor = array.getColor(R.styleable.HtTakephotoButton_innerCircleColor,Color.TRANSPARENT);
        progressColor = array.getColor(R.styleable.HtTakephotoButton_readColor,getResources().getColor(R.color.theme_color));
        mLoadingTime = array.getInteger(R.styleable.HtTakephotoButton_maxSeconds,30);
        mDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //单击
                isLongClick = false;
                if (listener != null) {
                    listener.onClick(HtTakephotoButton.this);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //长按
                isLongClick = true;
                postInvalidate();
                if (listener != null) {
                    listener.onLongClick(HtTakephotoButton.this);
                }
            }
        });
        mDetector.setIsLongpressEnabled(true);



    }

    private void resetParams() {
        width = getWidth();
        height = getHeight();
        circleWidth = width*0.13f;
        outRaduis = (float) (Math.min(width, height)/2.4);
        innerRaduis = outRaduis -circleWidth;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > height) {
            setMeasuredDimension(height, height);
        } else {
            setMeasuredDimension(width, width);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        resetParams();
        //画外圆
        outRoundPaint.setAntiAlias(true);
        outRoundPaint.setColor(outCircleColor);
        if (isLongClick){
            canvas.scale(1.2f,1.2f,width/2,height/2);
        }
        canvas.drawCircle(width/2,height/2, outRaduis, outRoundPaint);
        //画内圆
        innerRoundPaint.setAntiAlias(true);
        innerRoundPaint.setColor(innerCircleColor);
        if (isLongClick){
            canvas.drawCircle(width/2,height/2, innerRaduis /2.0f, innerRoundPaint);
            //画外原环
            mCPaint.setAntiAlias(true);
            mCPaint.setColor(progressColor);
            mCPaint.setStyle(Paint.Style.STROKE);
            mCPaint.setStrokeWidth(circleWidth/2);
            RectF rectF = new RectF(0+circleWidth,0+circleWidth,width-circleWidth,height-circleWidth);
            canvas.drawArc(rectF,startAngle,mSweepAngle,false,mCPaint);
        }else {
            canvas.drawCircle(width/2,height/2, innerRaduis, innerRoundPaint);
        }

    }

    public void start() {
        ValueAnimator animator = ValueAnimator.ofFloat(mmSweepAngleStart, mmSweepAngleEnd);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mSweepAngle = (float) valueAnimator.getAnimatedValue();
                //获取到需要绘制的角度，重新绘制
                invalidate();
            }
        });
        //这里是时间获取和赋值
        ValueAnimator animator1 = ValueAnimator.ofInt(mLoadingTime, 0);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int time = (int) valueAnimator.getAnimatedValue();
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator, animator1);
        set.setDuration(mLoadingTime * 1000);
        set.setInterpolator(new LinearInterpolator());
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                clearAnimation();
                isLongClick = false;
                postInvalidate();
                if (listener != null) {
                    listener.onFinish();
                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        switch(MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                isLongClick = false;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isLongClick) {
                    isLongClick = false;
                    postInvalidate();
                    if (this.listener != null) {
                        this.listener.onLongClickUp(this);
                    }
                }
                break;
        }
        return true;
    }

    private OnProgressTouchListener listener;

    public void setOnProgressTouchListener(OnProgressTouchListener listener) {
        this.listener = listener;
    }

    /**
     * 进度触摸监听
     */
    public interface OnProgressTouchListener {
        /**
         * 单击
         * @param photoButton
         */
        void onClick(HtTakephotoButton photoButton);

        /**
         * 长按
         * @param photoButton
         */
        void onLongClick(HtTakephotoButton photoButton);

        /**
         * 长按抬起
         * @param photoButton
         */
        void onLongClickUp(HtTakephotoButton photoButton);


        void onFinish();
    }

}

