package com.union_test.toutiao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.union_test.toutiao.R;

import java.util.ArrayList;

/**
 * created by wuzejian on 2019/7/24
 */
public class RadioButtonView extends View {


    public static final int BLUE= 0xff2196F3;

    final float k = 0.552284749831f;
    boolean updated=false;

    Paint mPaint;

    int width;
    int height;
    int length          =   0;
    int current         =   0;
    int old_current     =   0;
    int click_current   =   -1;
    float radius        =   -1;
    float eachWidth     =   0;
    float textSize;


    float margin        =   4.0f;
    int frameColor      =   BLUE;
    int textColor       =   Color.WHITE;
    float strokeWidth   =   2.0f;

    ArrayList<String> options    =   null;

    OnRadioButtonChangedListener listener = null;

    public RadioButtonView(Context context) {
        super(context);


    }

    public RadioButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width=MeasureSpec.getSize(widthMeasureSpec);
        height=MeasureSpec.getSize(heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width=w;
        height=h;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(!updated){
            initVariable();
            updated=true;
        }

        drawFrame(canvas);
        drawClickedFrame(canvas);
        drawText(canvas);


    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:

                float x1 = event.getX();
                float y1 = event.getY();

                if(y1>height/2.0f-radius&&y1<height/2.0f+radius&&x1>margin&&x1<width-margin){

                    click_current=(int)((x1-margin)/eachWidth);

                    postInvalidate();

                }


                break;

            case MotionEvent.ACTION_UP:

                float x2 = event.getX();
                float y2 = event.getY();

                if(y2>height/2.0f-radius&&y2<height/2.0f+radius&&x2>margin&&x2<width-margin){

                    int t = (int)((x2-margin)/eachWidth);

                    if(t==click_current){

                        current=t;

                        if(current!=old_current){

                            old_current=current;

                            if(listener!=null){
                                listener.onRadioButtonChanged(options.get(current),current);
                            }

                        }
                    }
                }

                click_current=-1;
                postInvalidate();

                break;

            default:break;

        }


        return true;
    }

    private void drawFrame(Canvas canvas){

        mPaint=new Paint();
        mPaint.setColor(frameColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(strokeWidth);

        for(int i=0;i<options.size();i++){

            if(i==current){
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            }
            else {
                mPaint.setStyle(Paint.Style.STROKE);
            }

            Path path=new Path();

            if(i==0){

                path.moveTo(margin,height/2.0f);

                path.cubicTo(margin,height/2.0f-radius*k,margin+radius*(1-k),height/2.0f-radius,margin+radius,height/2.0f-radius);
                path.lineTo(margin+eachWidth,height/2.0f-radius);
                path.lineTo(margin+eachWidth,height/2.0f+radius);
                path.lineTo(margin+radius,height/2.0f+radius);
                path.cubicTo(margin+radius*(1-k),height/2.0f+radius,margin,height/2.0f+radius*k,margin,height/2.0f);
                path.close();


            }else if(i==(options.size()-1)){

                path.moveTo(width-margin,height/2.0f);

                path.cubicTo(width-margin,height/2.0f-radius*k,width-margin-radius*(1-k),height/2.0f-radius,width-margin-radius,height/2.0f-radius);
                path.lineTo(width-margin-eachWidth,height/2.0f-radius);
                path.lineTo(width-margin-eachWidth,height/2.0f+radius);
                path.lineTo(width-margin-radius,height/2.0f+radius);
                path.cubicTo(width-margin-radius*(1-k),height/2.0f+radius,width-margin,height/2.0f+radius*k,width-margin,height/2.0f);
                path.close();

            }
            else {

                path.moveTo(margin+i*eachWidth,height/2.0f-radius);
                path.lineTo(margin+(i+1)*eachWidth,height/2.0f-radius);
                path.lineTo(margin+(i+1)*eachWidth,height/2.0f+radius);
                path.lineTo(margin+i*eachWidth,height/2.0f+radius);
                path.close();

            }

            canvas.drawPath(path,mPaint);

        }


    }

    private void drawClickedFrame(Canvas canvas){

        if(click_current!=-1){

            mPaint=new Paint();
            mPaint.setColor(frameColor);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            Path path=new Path();

            if(click_current==0){

                path.moveTo(margin,height/2.0f);

                path.cubicTo(margin,height/2.0f-radius*k,margin+radius*(1-k),height/2.0f-radius,margin+radius,height/2.0f-radius);
                path.lineTo(margin+eachWidth,height/2.0f-radius);
                path.lineTo(margin+eachWidth,height/2.0f+radius);
                path.lineTo(margin+radius,height/2.0f+radius);
                path.cubicTo(margin+radius*(1-k),height/2.0f+radius,margin,height/2.0f+radius*k,margin,height/2.0f);
                path.close();


            }else if(click_current==(options.size()-1)){

                path.moveTo(width-margin,height/2.0f);

                path.cubicTo(width-margin,height/2.0f-radius*k,width-margin-radius*(1-k),height/2.0f-radius,width-margin-radius,height/2.0f-radius);
                path.lineTo(width-margin-eachWidth,height/2.0f-radius);
                path.lineTo(width-margin-eachWidth,height/2.0f+radius);
                path.lineTo(width-margin-radius,height/2.0f+radius);
                path.cubicTo(width-margin-radius*(1-k),height/2.0f+radius,width-margin,height/2.0f+radius*k,width-margin,height/2.0f);
                path.close();

            }
            else {

                path.moveTo(margin+click_current*eachWidth,height/2.0f-radius);
                path.lineTo(margin+(click_current+1)*eachWidth,height/2.0f-radius);
                path.lineTo(margin+(click_current+1)*eachWidth,height/2.0f+radius);
                path.lineTo(margin+click_current*eachWidth,height/2.0f+radius);
                path.close();

            }

            canvas.drawPath(path,mPaint);


        }

    }


    private void drawText(Canvas canvas){

        mPaint=new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setTextSize(textSize);


        for(int i =0;i<options.size();i++){

            if(i==current||i==click_current){
                mPaint.setColor(textColor);
            }else {
                mPaint.setColor(frameColor);
            }

            String text=options.get(i);

            Rect textBound=new Rect();
            mPaint.getTextBounds(text,0,text.length(),textBound);

            canvas.drawText(text,margin+(i+0.5f)*eachWidth-textBound.width()/2.0f,height/2.0f+textBound.height()/2.5f,mPaint);

        }

    }

    private void initAttrs(AttributeSet attributeSet){

        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.RadioButtonView);

        margin=typedArray.getDimension(R.styleable.RadioButtonView_margin,4.0f);
        strokeWidth=typedArray.getDimension(R.styleable.RadioButtonView_strokeWidth,2.0f);
        frameColor=typedArray.getColor(R.styleable.RadioButtonView_frameColor,Color.WHITE);
        textColor=typedArray.getColor(R.styleable.RadioButtonView_textColor,BLUE);

        typedArray.recycle();



    }

    private void initVariable(){

        if(options==null||options.isEmpty()){
            options=getDefaultOptions();
        }

        length=options.size();
        eachWidth=(width-2*margin)/length ;

        float ew = (width-2*margin)/length;
        float eh = (height-2*margin)/2;

        radius = ew > eh ? eh : ew;

        textSize=radius*0.7f;

    }

    public void setMargin(float margin) {
        this.margin = margin;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public void setOnRadioButtonChangedListener(OnRadioButtonChangedListener listener) {
        this.listener = listener;
    }

    private ArrayList<String> getDefaultOptions(){

        ArrayList<String> defaultOptions=new ArrayList<>();
        defaultOptions.add("ON");
        defaultOptions.add("OFF");

        return defaultOptions;
    }

    public interface OnRadioButtonChangedListener{

        void onRadioButtonChanged(String option, int index);

    }
}
