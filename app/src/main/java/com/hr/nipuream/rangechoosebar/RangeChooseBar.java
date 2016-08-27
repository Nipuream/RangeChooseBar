package com.hr.nipuream.rangechoosebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 描述：仿人人车 范围选择器
 * 作者：Nipuream
 * 时间: 2016-08-26 15:05
 * 邮箱：571829491@qq.com
 */
public class RangeChooseBar extends View {

    private Paint grayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap normal,pressed;
    private Bitmap min,max;

    //旋转器的宽度
    private int ChooseBarWidth = 0;

    //分片 默认为10
    private int slice = 10;

    //每个分片的长度
    private int perSlice = 0;
    private int background_color ;
    private int choose_range_color;
    private int text_color ;
    private int paint_stoken_width ;
    private float textSize ;

    private String title = "车龄   (单位: 年)";


    public RangeChooseBar(Context context) {
        this(context,null);
    }

    public RangeChooseBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RangeChooseBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,context.getResources().getDisplayMetrics());
        normal = BitmapFactory.decodeResource(getResources(),R.mipmap.seek_thumb_normal);
        pressed = BitmapFactory.decodeResource(getResources(),R.mipmap.seek_thumb_pressed);

        min = normal;
        max = normal;

        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.RangeChooseBar);
        background_color = array.getColor(R.styleable.RangeChooseBar_rangebar_background,Color.parseColor("#CBCBCB"));
        choose_range_color = array.getColor(R.styleable.RangeChooseBar_rangebar_choose_background,Color.parseColor("#FC511C"));
        text_color = array.getColor(R.styleable.RangeChooseBar_rangebar_text_color,Color.parseColor("#505050"));
        paint_stoken_width = array.getInteger(R.styleable.RangeChooseBar_rangebar_stoken_width,8);
        this.textSize = array.getDimension(R.styleable.RangeChooseBar_rangebar_text_size,textSize);
        ChooseBarWidth = (int) array.getDimension(R.styleable.RangeChooseBar_rangebar_width,dip2px(context,40));
        slice = array.getInteger(R.styleable.RangeChooseBar_rangebar_slice,10);
        title = array.getString(R.styleable.RangeChooseBar_rangebar_title);
        array.recycle();

        grayPaint.setColor(background_color);
        yellowPaint.setColor(choose_range_color);
        unitPaint.setColor(text_color);
        grayPaint.setStrokeWidth(paint_stoken_width);
        yellowPaint.setStrokeWidth(paint_stoken_width);
        yellowPaint.setTextSize(this.textSize);
        unitPaint.setTextSize(this.textSize);

    }

    private int paddingTop,paddingLeft,paddingBottom,
            paddingRight,drawWidth,drawHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(mode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSize,200);
        }

        paddingTop = getPaddingTop();
        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        paddingRight = getPaddingRight();

        drawWidth = getWidth() - paddingLeft - paddingRight;
        drawHeight = getHeight() - paddingTop - paddingBottom;

        MinOriginLeft = getPaddingLeft();
        MinOriginRight = ChooseBarWidth;
        MaxOriginLeft = drawWidth-ChooseBarWidth;
        MaxOriginRight = drawWidth;

        perSlice = (drawWidth-ChooseBarWidth)/slice;
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(getPaddingLeft()+ ChooseBarWidth/2,drawHeight/4 * 3,drawWidth-ChooseBarWidth/2,drawHeight/4 * 3,grayPaint);
        canvas.drawLine(MinOriginRight - ChooseBarWidth/2,drawHeight/4 * 3,MaxOriginLeft + ChooseBarWidth/2,drawHeight/4 * 3,yellowPaint);

        minRect = new Rect(MinOriginLeft,drawHeight/4 * 3-ChooseBarWidth/2,MinOriginRight,drawHeight/4 * 3+ChooseBarWidth/2);
        canvas.drawBitmap(min,null,minRect,grayPaint);

        maxRect = new Rect(MaxOriginLeft,drawHeight/4 * 3-ChooseBarWidth/2,MaxOriginRight,drawHeight/4 * 3+ChooseBarWidth/2);
        canvas.drawBitmap(max,null,maxRect,grayPaint);

        int MinPoi = (MinOriginLeft + MinOriginRight)/2;
        int MaxPoi = (MaxOriginLeft + MaxOriginRight)/2;


        int MinCurrentSlice = MinPoi/perSlice;
        int MaxCurrentSlice = MaxPoi/perSlice;

        float MinCurrentSliceValue = yellowPaint.measureText(String.valueOf(MinCurrentSlice));
        float MaxCurrentSliceValue = yellowPaint.measureText(String.valueOf(MaxCurrentSlice));

        canvas.drawText(String.valueOf(MinCurrentSlice),MinPoi-MinCurrentSliceValue/2,drawHeight/2,yellowPaint);
        canvas.drawText(String.valueOf(MaxCurrentSlice),MaxPoi-MaxCurrentSliceValue/2,drawHeight/2,yellowPaint);

        canvas.drawText(title,paddingLeft,drawHeight/4,unitPaint);

    }

    private Rect minRect,maxRect;

    private boolean isMinPress = false;
    private boolean isMaxPress = false;

    private int MinOriginLeft,MinOriginRight,MaxOriginLeft,MaxOriginRight = 0;


    public void setTitle(String title){
        this.title = title;
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float x = event.getX();
            float y = event.getY();

            if(x > minRect.left && x < minRect.right &&
                    y > minRect.top && y < minRect.bottom){
                if(!isMaxPress) {
                    min = pressed;
                    isMinPress = true;
                }
                invalidate();
            }

            if(x > maxRect.left && x < maxRect.right
                    && y > maxRect.top && y < maxRect.bottom){
                if(!isMinPress) {
                    max = pressed;
                    isMaxPress = true;
                }
                invalidate();
            }
        }
        return super.dispatchTouchEvent(event);
    }


    private float lastX = 0;
    private int offRightX = 0;
    private int offLeftX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(isMaxPress || isMinPress)
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                {
                    lastX = event.getX();
                }
                return true;
                case MotionEvent.ACTION_MOVE:
                {

                    float moveX = event.getX();
                    moveX -= lastX;

                    if(isMinPress){
                        offRightX = (int) moveX;

                        if(MinOriginLeft >= paddingLeft ){
                            //快度向左滑动处理
                            if(MinOriginLeft + offRightX < paddingLeft){
                                MinOriginLeft = paddingLeft;
                                MinOriginRight = ChooseBarWidth;
                                invalidate();
                            }
                            else{
                                if(MinOriginRight <= drawWidth){

                                    if(MinOriginRight + offRightX >drawWidth){

                                        MinOriginRight = drawWidth;
                                        MinOriginLeft = drawWidth - ChooseBarWidth;
                                        invalidate();

                                    }else{
                                        //缓慢滑动正常情况下
                                        MinOriginLeft += offRightX;
                                        MinOriginRight += offRightX;
                                        invalidate();
                                    }
                                }
                            }
                        }
                    }

                    if(isMaxPress){
                        offLeftX = (int) moveX;

                        if(MaxOriginRight <= drawWidth){

                            if(MaxOriginRight + offLeftX > drawWidth){
                                MaxOriginRight =  drawWidth;
                                MaxOriginLeft = MaxOriginRight - ChooseBarWidth;
                                invalidate();
                            }else{
                                if(MaxOriginLeft >= paddingLeft){
                                    if(MaxOriginLeft + offLeftX < paddingLeft){
                                        MaxOriginLeft = paddingLeft;
                                        MaxOriginRight = paddingLeft + ChooseBarWidth;
                                        invalidate();
                                    }else{
                                        MaxOriginLeft += offLeftX;
                                        MaxOriginRight += offLeftX;
                                        invalidate();
                                    }
                                }
                            }
                        }
                    }
                    lastX = event.getX();
                }
                return true;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                {

                    min = normal;
                    max = normal;

                    //判断bar 所处分片位置
                    if(isMaxPress)
                    {

                        int MaxcurrentSlice = getMaxCurrentSlice();
                        int MincurrentSlice = getMinCurretnSlice();

                        //此时位于同一分片
                        if(MaxcurrentSlice == MincurrentSlice)
                             if(MaxOriginLeft >= MinOriginLeft)
                                 MaxcurrentSlice += 1;
                             else
                                 MaxcurrentSlice -= 1;


                        int afterValue = MaxcurrentSlice * perSlice;
                        MaxOriginLeft = afterValue - ChooseBarWidth/2 + ChooseBarWidth/2;
                        MaxOriginRight = afterValue + ChooseBarWidth /2 + ChooseBarWidth/2;

                    }

                    if(isMinPress)
                    {
                        int MincurrentSlice = getMinCurretnSlice();
                        int MaxcurrentSlice = getMaxCurrentSlice();

                        if(MincurrentSlice == MaxcurrentSlice)
                            if(MinOriginRight <= MaxOriginRight)
                                MincurrentSlice -= 1;
                            else
                                MincurrentSlice += 1;

                        int afterValue = MincurrentSlice*perSlice;
                        MinOriginLeft = afterValue - ChooseBarWidth/2 +ChooseBarWidth/2;
                        MinOriginRight = afterValue + ChooseBarWidth/2 + ChooseBarWidth/2;
                    }

                    if(l != null)
                        l.chooseResult(Math.min(getMinCurretnSlice(),getMaxCurrentSlice()),
                                Math.max(getMinCurretnSlice(),getMaxCurrentSlice()));

                    invalidate();
                    isMaxPress = false;
                    isMinPress = false;
                    lastX = 0;
                }
                return false;
            }

        return super.onTouchEvent(event);
    }


    private int getMaxCurrentSlice(){
        int slicePoi = (MaxOriginLeft + MaxOriginRight)/2 - ChooseBarWidth/2;
        int MaxcurrentSlice = slicePoi/perSlice;
        int grapValue = slicePoi%perSlice;

        MaxcurrentSlice = (grapValue>=perSlice/2)?
                MaxcurrentSlice+1:MaxcurrentSlice;
        return MaxcurrentSlice;
    }

    private int getMinCurretnSlice(){
        int slicePoi = (MinOriginLeft + MinOriginRight)/2 - ChooseBarWidth/2;

        int MincurrentSlice = slicePoi/perSlice;
        int grapValue = slicePoi%perSlice;
        MincurrentSlice = (grapValue>=perSlice/2)?
                MincurrentSlice+1:MincurrentSlice;
        return MincurrentSlice;
    }


    public interface RangeChooseListener{
        void chooseResult(int min,int max);
    }

    private RangeChooseListener l;

    public void setOnRangeChooseListener(RangeChooseListener l){
        this.l = l;
    }


}
