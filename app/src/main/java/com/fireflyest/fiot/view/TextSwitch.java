package com.fireflyest.fiot.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.fireflyest.fiot.R;
import com.fireflyest.fiot.util.DpOrPxUtil;

import java.util.Arrays;


public class TextSwitch extends View {

    private static final String TAG = "TextSwitch";

    public static final int EMPTY = -1;
    public static final int IDLE = 0;
    public static final int SCROLLING = 1;


    private String[] strings = {"开启", "关闭"};
    private int select = 0;
    private int currentSelect = 0;
    private float scrollX = 0;
    private float dragX = 0;
    private float dragMax = 0;
    private float left;
    private float right;
    private int state = EMPTY;
    private float selectWidth = 0;
    private int duration = 260;

    private SwitchChangeListener changeListener;

    private float joinWidth;
    private float defaultHeight;
    private float defaultWidth;
    private float barWidth;
    private float barLength;
    private boolean barScroll;
    private boolean barDrag;
    private float side;

    final private Paint barPaint = new Paint();
    final private Paint textPaint = new Paint();
    final private Paint selectTextPaint = new Paint();

    public interface SwitchChangeListener{
        void onSwitchChange(int var);
    }

    public TextSwitch(Context context) {
        super(context);
    }

    public TextSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setTextArray(String[] textArray){
        this.strings = textArray;
        this.invalidate();
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setChangeListener(SwitchChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public int getSelect() {
        return select;
    }

    /**
     * 切换动画
     * @param amount 切换多少个位置
     */
    public void switchAnimation(int amount){
        currentSelect = select - amount;
        if (currentSelect < 0) currentSelect = 0;
        if (currentSelect > strings.length -1)currentSelect = strings.length -1;
        float leftStart, leftEnd, rightStart, rightEnd;
        leftStart = left;
        leftEnd = joinWidth+1+currentSelect*selectWidth;
        rightStart = right;
        rightEnd = joinWidth+1+(currentSelect+1)*selectWidth;
        if(barLength != 0){
            leftEnd+=side;
            rightEnd-=side;
        }
        ValueAnimator leftAnimator = ValueAnimator.ofFloat(leftStart, leftEnd);
        ValueAnimator rightAnimator = ValueAnimator.ofFloat(rightStart, rightEnd);
        if(amount < 0){
            leftAnimator.setDuration(duration);
            rightAnimator.setDuration(duration/2);
            leftAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    select = currentSelect;
                    if(changeListener != null) changeListener.onSwitchChange(select);
                    super.onAnimationEnd(animation);
                }
            });
        }else {
            leftAnimator.setDuration(duration/2);
            rightAnimator.setDuration(duration);
            rightAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    select = currentSelect;
                    if(changeListener != null) changeListener.onSwitchChange(select);
                    super.onAnimationEnd(animation);
                }
            });
        }
        leftAnimator.addUpdateListener(animation -> {
            left = (float) animation.getAnimatedValue();
            invalidate();
        });
        rightAnimator.addUpdateListener(animation -> {
            right = (float) animation.getAnimatedValue();
            invalidate();
        });

        leftAnimator.start();
        rightAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: " + Arrays.toString(strings));
        float width = getWidth(), height = getHeight();

        float start = joinWidth+1, end = joinWidth-1;
        if(selectWidth == 0) selectWidth = (width-2*(joinWidth+1))/strings.length;



        if(state == EMPTY) {
            left = start+select*selectWidth;
            right = left + selectWidth;
            if(barLength != 0){
                side = (selectWidth-start-end-barLength)/2;
                left+=side;
                right-=side;
            }
            state = IDLE;
        }

        // 背景
        if(barWidth == 0){
            canvas.drawRoundRect(
                    left+dragX,
                    start,
                    right+dragX,
                    height - end,
                    joinWidth,
                    joinWidth,
                    barPaint
            );
        }else {
            canvas.drawRoundRect(
                    left+dragX,
                    height -barWidth - end/1.2F,
                    right+dragX,
                    height - end/1.2F,
                    joinWidth,
                    joinWidth,
                    barPaint
            );
        }
        // 字
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
        float i = 0;
        for(String s : strings){
            if(select == i){
                canvas.drawText(s, joinWidth+1+selectWidth*(i+0.5F)+dragX, height/2+distance, selectTextPaint);
            }else {
                canvas.drawText(s, joinWidth+1+selectWidth*(i+0.5F)+dragX, height/2+distance, textPaint);
            }
            i++;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width = getSuggestedMinimumWidth(), height = getSuggestedMinimumHeight();

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST){
            width = strings.length * defaultWidth;
        }else if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY){
            width = MeasureSpec.getSize(widthMeasureSpec);
            if(width > strings.length * defaultWidth) {
                selectWidth = defaultWidth;
                width = strings.length * defaultWidth + joinWidth*2;
            }else {
//                barDrag = true;
                dragMax = strings.length * defaultWidth - width;
            }
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST){
            height = defaultHeight;
        }else if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY){
            height = MeasureSpec.getSize(heightMeasureSpec);
            if(height > defaultHeight){
                height = defaultHeight;
            }
        }

        setMeasuredDimension((int)width, (int)height);
    }

    private final GestureDetector detector = new GestureDetector(this.getContext(), new TouchListener());

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_UP && state == SCROLLING){
            state = IDLE;
            float amount = scrollX/selectWidth;
            amount += amount > 0 ? 0.5 :-0.5;
            scrollX = 0;
            switchAnimation((int)amount);
            performClick();
            return true;
        }else {
            return detector.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void init(Context context, @Nullable AttributeSet attrs){
        joinWidth = DpOrPxUtil.dip2px(context, 10F);
        defaultWidth = DpOrPxUtil.dip2px(context, 60F);
        defaultHeight = DpOrPxUtil.dip2px(context, 50F);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Bar, 0, 0);
        barWidth = typedArray.getDimension(R.styleable.Bar_barWidth, 0);
        barLength = typedArray.getDimension(R.styleable.Bar_barLength, 0);
        barScroll = typedArray.getBoolean(R.styleable.Bar_barScroll, true);
        int barColor = typedArray.getColor(R.styleable.Bar_barColor, Color.parseColor("#88E0E0E0"));

        typedArray.recycle();

        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setAntiAlias(true);
        barPaint.setStrokeWidth(joinWidth);
        barPaint.setColor(barColor);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(joinWidth);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);

        selectTextPaint.setStyle(Paint.Style.FILL);
        selectTextPaint.setStrokeWidth(joinWidth);
        selectTextPaint.setTextSize(45);
        selectTextPaint.setFakeBoldText(true);
        selectTextPaint.setAntiAlias(true);
        selectTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    class TouchListener extends GestureDetector.SimpleOnGestureListener {

        private int plus;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(barScroll) {
                state = SCROLLING;

                //判断滑动方向
                if(plus == 0) plus = distanceX > 0 ? 1 : -1;

                //判断有没有回滑
                if(distanceX > 0 == (plus > 0)){
                    if(plus > 0){
                        left -= distanceX;
                        right -= distanceX/2;
                    }else {
                        left -= distanceX/2;
                        right -= distanceX;
                    }
                }else {
                    if(plus > 0){
                        left -= distanceX/2;
                        right -= distanceX/4;
                    }else {
                        left -= distanceX/4;
                        right -= distanceX/2;
                    }
                }

                //累计滑动
                scrollX += distanceX;

                //判断回滑是否过头
                if(scrollX * plus < 0 - selectWidth) plus = 0;
                invalidate();
            }else if(barDrag){
                dragX -= distanceX;
                System.out.println(dragX);
                if(dragX < -dragMax){
                    dragX = -dragMax;
                }else if(dragX > 0){
                    dragX = 0;
                }
                invalidate();
            }

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int amount = select - (int)(e.getX()/selectWidth);
            if (state != SCROLLING && amount != 0) {
                switchAnimation(amount);
            }
            plus = 0;
            return true;
        }


    }




}
